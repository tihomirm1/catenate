package com.ddr.sync.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Calendar;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ddr.gen.R;
import com.ddr.utils.StringUT;

public class MyFTPManager {

    public static final String DIR_IN = "/in";
    public static final String DIR_OUT = "/out";
    public static final String DIR_CONVERT = "/convert";
    public static final String DIR_UPDATE = "/update";
    public static final String DIR_MESSAGES = "/messages";

    public static final String FILE_SALES = "sales.csv";
    public static final String FILE_SALELINES = "salesLines.csv";

    public static final Charset CharSet_Win1251 = Charset.forName("Windows-1251");
    public static final String[] saleExportFileNames = new String[] { FILE_SALES, FILE_SALELINES };

    private String TAG;
    private String TAGCUR;
    private String datePart;
    private FTPClient ftp;
    private AuthResponse mAuth;

    public MyFTPManager(AuthResponse auth) {
        this.mAuth = auth;
    }

    public MyFTPResponse connectFTP(FTPClient ftp, String initialDir) {

        //This is where the TLS/SSL is going be instantiated.
        //need to fetch socket to client.

        MyFTPResponse response = new MyFTPResponse();
        this.ftp = ftp;
        try {
            ftp.setBufferSize(1024000);
            InetAddress address = InetAddress.getByName(mAuth.getServiceAddress());

            ftp.connect(address);
            if (ftp.login(mAuth.getUsername(), mAuth.getPassword())) {
                ftp.setControlKeepAliveTimeout(300);
                ftp.enterLocalPassiveMode(); // important!
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.changeWorkingDirectory(initialDir);
                response.setSuccess(true);
                return response;
            }
            Log.w(TAG, TAGCUR + "Coubld not Log in to FTP.");
            response.setMessage(R.string.msg_unsuccessfull_login);

        } catch (IOException e1) {
            Log.e(TAG, TAGCUR + e1.toString());
            if (ftp.isConnected()) {
                response.setMessage(R.string.msg_err_while_authenticating);
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    Log.e(TAG, TAGCUR + e.toString());
                }
            } else {
                response.setMessage(R.string.msg_unable_to_connect_to_ftp);
            }
        }
        response.setSuccess(false);
        return response;
    }

    public void disconnectFTP() {
        if (ftp != null) {
            FTPClient temp = ftp;
            ftp = null;
            attemptDisconnect(temp, 1);
        }
    }

    private void attemptDisconnect(final FTPClient temp, final int loopCount) {

        new AsyncTask<Void, Void, Void>() {

            int count = loopCount;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (temp.isConnected()) {
                        temp.logout();
                        temp.disconnect();
                    }
                } catch (IOException e) {
                    Log.e(TAG, TAGCUR + e.toString());
                    if (count <= 2) {
                        attemptDisconnect(temp, ++count);
                    }
                }
                return null;
            }

        }.execute();
    }

    public MyFTPResponse checkServerIsReady(MyFTPResponse response) {
        try {
            if (ftp.changeWorkingDirectory("/" + mAuth.getUser() + DIR_OUT)) {
                FTPFile[] files = ftp.listFiles();
                for (FTPFile file : files) {
                    for (String expFileName : saleExportFileNames) {
                        if (file.getName().equals(expFileName)) {
                            response.setSuccess(false);
                            response.setMessage(R.string.msg_unsync_data_on_server);
                            break;
                        }
                    }
                }
                ftp.changeWorkingDirectory("/" + mAuth.getUser() + DIR_IN);
            } else {
                response.setSuccess(false);
                response.setMessage(R.string.msg_unsucc_navigation_in_dir);
            }
        } catch (IOException e) {
            Log.w(TAG, TAGCUR + e.getMessage());
            response.setSuccess(false);
            response.setMessage(R.string.msg_problem_during_syncheck);
        }

        return response;
    }

    public boolean uploadFile(Context cxt, String dir, String fileName, File file) throws IOException {
        boolean result = false;
        BufferedInputStream in = null;
        try {
            if (file == null) {
                in = new BufferedInputStream(cxt.openFileInput(fileName));
            } else {
                in = new BufferedInputStream(new FileInputStream(file));
            }
            ftp.changeWorkingDirectory("/" + mAuth.getUser() + dir);
            result = ftp.storeFile(fileName, in);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                in.close();

            if (file == null) {
                cxt.deleteFile(fileName);
            } else if (file.exists()) {
                file.delete();
            }
        }
        return result;
    }

    public String getDatePart() {
        if (!StringUT.isValid(datePart)) {
            Calendar c = Calendar.getInstance();
            int h = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);

            datePart = "_" + h + "-" + min + "-" + day + "-" + (month + 1);
        }
        return datePart;
    }

    public String getUserID() {
        return mAuth.getUser();
    }

    public InputStream getImputStream(String fileName) {
        try {
            return ftp.retrieveFileStream(fileName);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public boolean checkDirForFile(String dir, String fileName) {
        try {
            ftp.changeWorkingDirectory(dir);
            return checkCurrentDirForFile(fileName);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return false;
    }

    public boolean checkCurrentDirForFile(String fileName) {
        try {
            String[] filesOnServer = ftp.listNames();
            for (String file : filesOnServer) {
                if (file.equals(fileName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return false;
    }
}

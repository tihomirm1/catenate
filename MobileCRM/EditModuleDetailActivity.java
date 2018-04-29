package com.paraflow.mobilecrm;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Messenger;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.R.layout.simple_spinner_item;
import static com.paraflow.mobilecrm.ModuleFields.MEETING_MOL_C;
import static com.paraflow.mobilecrm.RestUtilConstants.NAME;
import static com.paraflow.mobilecrm.SugarCRMContent.MeetingsColumns.ACCOUNT_NAME;
import static com.paraflow.mobilecrm.SugarCRMContent.MeetingsColumns.DESCRIPTION;
import static com.paraflow.mobilecrm.SugarCRMContent.MeetingsColumns.NEXT_MEETING_C;
import static com.paraflow.mobilecrm.SugarCRMContent.MeetingsColumns.START_DATE;
import static com.paraflow.mobilecrm.SugarCRMContent.MeetingsColumns.STATUS;
import static com.paraflow.mobilecrm.SugarCRMContent.RECORD_ID;

/**
 * EditModuleDetailActivity
 *
 */
public class EditModuleDetailActivity extends Activity {

    private final static String TAG = "EditModuleDetail";
    private static final java.util.UUID UUID = null ;

    private int MODE = -1;

    private ViewGroup mDetailsTable;

    private ViewGroup spinnerView;

    private Cursor mCursor;

    private String mSugarBeanId;

    private String mModuleName;

    private String mRowId;

    private int importFlag;

    private ViewConfiguration.FieldConfiguration[] mSelectFields;

    private DatabaseHelper mDbHelper;

    private EditModuleDetailActivity.LoadContentTask mTask;

    private String mAccountName = "";

    private String mUserName;

    private ProgressDialog mProgressDialog;

    private boolean hasError;

    private RelativeLayout mParent;

    private Context context;

    private String quickCreateModuleRel = null;

    private String createRecordId = null;
    
    private DatabaseOperations dbOper;

    SharedPreferences prefs = null;

    private String comeFromModule;

    private ViewGroup viewGroupActivity;

    private String newRecordGuid = null;

    private String lead_id_c = null;

    private String account_id_c = null;

    private Map<String, String> modifiedValues = new LinkedHashMap<String, String>();

    private DatabaseHelper databaseHelper;

    private SQLiteDatabase db;

    private String userName;

    private String assignedUserSugarId;

    private Map<String, String> company_info;

    private String value_meeting_mol = null;

    private HashMap hidden_fields = new HashMap();

    public String field_name_listener;

    private String account_guid = "";




    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = this;

        databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();

        this.setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        setContentView(R.layout.edit_details);


        mDetailsTable = (ViewGroup) findViewById(R.id.moduleDetailsTable);
        databaseHelper = new DatabaseHelper(context);
        prefs = context.getSharedPreferences("com.paraflow.mobilecrm", MODE_PRIVATE);

        userName = prefs.getString("username", null);
        assignedUserSugarId = prefs.getString("user_sugar_id", null);


        mDbHelper = new DatabaseHelper(context);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        dbOper = new DatabaseOperations(context);
        mModuleName = Util.CONTACTS;

        if (extras != null) {
            // i always get the module name
            mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);
            importFlag = extras.getInt(Util.IMPORT_FLAG);
            mRowId = intent.getStringExtra(Util.ROW_ID);
            mSugarBeanId = intent.getStringExtra(RestUtilConstants.BEAN_ID);

            createRecordId = intent.getStringExtra(RestUtilConstants.CREATE_RECORD_ID);
            mAccountName = intent.getStringExtra(RestUtilConstants.REL_ACCOUNT_NAME);
            comeFromModule = intent.getStringExtra(RestUtilConstants.COME_FROM);
            account_guid = intent.getStringExtra("account_guid");

        }

        if (mRowId != null) {
            MODE = Util.EDIT_MODE;
        } else {
            MODE = Util.NEW_MODE;
        }

        mSelectFields = ContentUtils.getModuleProjectionsNew(mModuleName);
        mTask = new EditModuleDetailActivity.LoadContentTask(context);
        mTask.execute(null, null, null);

//========================Bottom Navigation===new method========================================

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation_edit);

        switch(mModuleName){
            case "Leads":
                bottomNavigationView.getMenu().removeItem(R.id.action_questions);
                bottomNavigationView.getMenu().removeItem(R.id.action_new_ppz);
                bottomNavigationView.getMenu().removeItem(R.id.action_save);

                break;


            case "Meetings":
                bottomNavigationView.getMenu().removeItem(R.id.action_new_ppz);
                bottomNavigationView.getMenu().removeItem(R.id.action_questions);
                bottomNavigationView.getMenu().removeItem(R.id.action_save);
                break;


            case "Account":

                break;
        }



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.action_save:
                        //String mode_string = "save";
                        //saveModuleItem(getCurrentFocus(), mode_string);


                        break;
                    case R.id.action_home:

                        break;

                    case R.id.action_save_close:

                        String mode_string_save = "saveandclose";
                        saveModuleItem(getCurrentFocus(), mode_string_save);

                        break;

                    case R.id.action_questions:



                        break;

                    case R.id.action_new_ppz:
                        //Toast.makeText(context, "Модул ППЗ се разработва в момента!", Toast.LENGTH_LONG).show();

                        break;

                    default:

                        break;
                }
                return true;

            }
        });


    }





    class LoadContentTask extends AsyncTask<Object, Object, Object> {

        int staticRowsCount;
        Context mContext;

        final static int STATIC_ROW = 1;

        final static int DYNAMIC_ROW = 2;

        final static int SAVE_BUTTON = 3;

        final static int INPUT_TYPE = 4;

        LoadContentTask(Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            mProgressDialog = ViewUtil.getProgressDialog(context, getString(R.string.loading), false);
            mProgressDialog.show();
        }




        @Override
        protected void onProgressUpdate(Object... values) {


            super.onProgressUpdate(values);
            final String fieldName = (String) values[1];


            String editTextValue = null;
            View editRow = (View) values[2];
            editRow.setVisibility(View.VISIBLE);

//====
            if(editRow.getParent()!=null)
                ((ViewGroup)editRow.getParent()).removeView(editRow); // <- fix
            mDetailsTable.addView(editRow);
//====

            field_name_listener = values[1].toString();
            TextView labelView = (TextView) values[3];
            labelView.setText((String) values[4]);
            String type = (String) values[7];


            if (type.equals("text")){

                    AutoCompleteTextView valueView = (AutoCompleteTextView) values[5];
                    //editTextValue = mAccountName;
                    editTextValue = (String) values[6];

                    if(Util.MEETINGS.equals(mModuleName) && field_name_listener.equals(ModuleFields.ACCOUNT_NAME) && MODE == Util.EDIT_MODE){


                        mAccountName = editTextValue;

                        valueView.setInputType(InputType.TYPE_NULL);

                    }else if(Util.MEETINGS.equals(mModuleName) && field_name_listener.equals(ModuleFields.ACCOUNT_NAME) && MODE == Util.NEW_MODE){

                        editTextValue = mAccountName;

                        valueView.setInputType(InputType.TYPE_NULL);


                    }
                    else if(Util.MEETINGS.equals(mModuleName) && field_name_listener.equals("name") && MODE == Util.NEW_MODE){

                        editTextValue = "Нова среща, създадена в приложението.";

                    }else{

                        valueView.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                    }

                valueView.setText(editTextValue);



            }
            else if( type.equals("date") ){

                AutoCompleteTextView valueView = (AutoCompleteTextView) values[5];
                valueView.setTag(fieldName);

                valueView.setInputType(InputType.TYPE_NULL);


                valueView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //new ReScheduleAction();
                        Calendar calendar = new GregorianCalendar();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int date = calendar.get(Calendar.DATE);



                        if(fieldName.equals(ModuleFields.NEXT_MEETING_C) && Util.MEETINGS.equals(mModuleName)){

                            new DatePickerDialog(context, new EditModuleDetailActivity.DateNextMeetings(v), year, month, date).show();

                        }else if(fieldName.equals(ModuleFields.DATE_START) && Util.MEETINGS.equals(mModuleName)){

                            new DatePickerDialog(context, new EditModuleDetailActivity.DateListenerResched(v), year, month, date).show();


                        }

                    }
                });
                //valueView.callOnClick(new ReScheduleAction());

                if (MODE == Util.NEW_MODE && field_name_listener.equals("date_start")){

                    String today =  DateUT.formatDateT(DateUT.getToday());
                    String getTimeNow1 =  DateUT.getTimeNow();
                    editTextValue = today + " " + getTimeNow1;


                }else if(MODE == Util.NEW_MODE && field_name_listener.equals(ModuleFields.NEXT_MEETING_C)){

                    String date_next = DateUT.calculateDateWithDelayT(14);
                    editTextValue = date_next + " 08:00:00";

                }else{

                    editTextValue = (String) values[6];
                }

                valueView.setText(editTextValue);

            }

            else if (type.equals("option")) {

                Spinner valueView = (Spinner) values[5];
                valueView.setTag(fieldName);
                int value_spinner = (int) values[6];
                valueView.setSelection(value_spinner);

            }



        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (MODE == Util.EDIT_MODE) {

                    DatabaseOperations dbOp = new DatabaseOperations(context);
                    mCursor = dbOp.select_detail_view_data( mModuleName , mRowId );

                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return Util.FETCH_FAILED;
            }

            return Util.FETCH_SUCCESS;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            setContents();


            if (mCursor != null && !mCursor.isClosed())
                mCursor.close();
            if (isCancelled())
                return;
            int retVal = (Integer) result;
            switch (retVal) {
                case Util.FETCH_FAILED:
                    break;
                case Util.FETCH_SUCCESS:

                    break;
                default:
            }

            mProgressDialog.cancel();
        }


        private void setContents() {


            if (MODE == Util.EDIT_MODE){
                if (!isCancelled()) {
                    mCursor.moveToFirst();
                    mSugarBeanId = mCursor.getString(1); // beanId has

                }
            }else{

                mCursor = null;
            }



            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int rowsCount = 0; // to keep track of number of rows being used


            for ( ViewConfiguration.FieldConfiguration item : mSelectFields ) {

                if(item.edit_view == true){

                    View tableRow;
                    ViewGroup tableRowLabel;
                    TextView textViewForLabel;
                    AutoCompleteTextView editTextForValue;
                    String value = null;
                    String command = "";

                    if(item.type.equals("text")){


                            tableRow = inflater.inflate(R.layout.edit_table_row, mDetailsTable, false);
                            textViewForLabel = (TextView) tableRow.findViewById(R.id.editRowLabel);
                            editTextForValue = (AutoCompleteTextView) tableRow.findViewById(R.id.editRowValue);


                            if(mCursor != null){ 
                                value = mCursor.getString(mCursor.getColumnIndex(item.name));
                            }

                            if((value == null) || (value.equals("null"))){
                                value = "";
                            }

                            publishProgress(command, item.name, tableRow, textViewForLabel, item.label, editTextForValue, value, "text");




                    }else if(item.type.equals("option")){
                        int indexSpinner = 0;
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, simple_spinner_item, item.options);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                        View spinnerView = inflater.inflate(R.layout.edit_table_spinner, mDetailsTable, false);
                        textViewForLabel = (TextView) spinnerView.findViewById(R.id.editRowLabel);
                        Spinner editTextForValue2 = (Spinner) spinnerView.findViewById(R.id.meeting_planned_spinner);


                        editTextForValue2.setAdapter(adapter);

                        //====
                        if(item.name.equals("meeting_mol_c")){

                            if(mCursor != null){

                                value = mCursor.getString(mCursor.getColumnIndex(item.name));

                                if(value.equals("yes")){
                                    indexSpinner = 0;
                                }else{

                                    indexSpinner = 1;
                                }
                            }

                        }else if(item.name.equals("status")){

                            if(mCursor != null){

                                value = mCursor.getString(mCursor.getColumnIndex(item.name));

                                if(value.equals("Planned")){
                                    indexSpinner = 0;
                                }else{

                                    indexSpinner = 1;
                                }
                            }
                        }
                        //====
                        publishProgress(command, item.name, spinnerView, textViewForLabel, item.label, editTextForValue2, indexSpinner, "option");

                    }else if(item.type.equals("date")){

                        tableRow = inflater.inflate(R.layout.edit_table_row, mDetailsTable, false);
                        textViewForLabel = (TextView) tableRow.findViewById(R.id.editRowLabel);
                        editTextForValue = (AutoCompleteTextView) tableRow.findViewById(R.id.editRowValue);

                        if(mCursor != null){ //to change it, according to view mode

                            value = mCursor.getString(mCursor.getColumnIndex(item.name));

                        }

                        if((value == null) || (value.equals("null"))){

                            value = "";

                        }

                        publishProgress(command, item.name, tableRow, textViewForLabel, item.label, editTextForValue, value, "date");


                    }else if(item.type.equals("hidden_field")){


                        if (MODE == Util.EDIT_MODE){

                            String key = item.name;
                            value = mCursor.getString(mCursor.getColumnIndex(item.name));
                            hidden_fields.put(key, value);

                        }else{

                            if(mModuleName.equals("Meetings")){

                                if(comeFromModule.equals("Leads")){

                                    hidden_fields.put("lead_id_c", account_guid);

                                }else{

                                    hidden_fields.put("account_id_c", account_guid);

                                }
                            }

                        }
                    }
                }
            }
        }
    }



    @Override
    public void onPause() {
        super.onPause();

        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }

    }



    public Dialog onCreateDialog(int id) {

        return new AlertDialog.Builder(this).setTitle(id).setMessage(R.string.discardAlert).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        }).create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                break;
            case R.id.action_home:

                break;
            default:

                break;
        }
        return true;
    }


    private class DateNextMeetings implements DatePickerDialog.OnDateSetListener {
        View mView;

        public DateNextMeetings(View view) {
            mView = view;
        }

        public void onDateSet(DatePicker view, int year, int month, int monthDay) {

            String month_big = null;
            int month_plus = month + 1;

            String day_p = null;
            String sting_day = null;
            sting_day = String.valueOf(monthDay);

            if(monthDay < 10){

                day_p = '0'+sting_day;

            }
            else{
                day_p = sting_day;
            }

            if(!(month_plus >= 10)){

                month_big = "0"+ month_plus;
                String date_selection = year + "-" + month_big + "-" + day_p;
                String datetime_selection = date_selection + " 08:00:00";
                RelativeLayout rewd = mParent;

                View no = viewGroupActivity.findViewWithTag("next_meeting_c");
                int dsa = no.getId();
                EditText dd = (EditText) no.findViewById(dsa);
                Editable current_date = dd.getText();
                dd.setText(datetime_selection);

                //}

            }else{

                String date_selection = year + "-" + month_plus + "-" + day_p;

                View no = viewGroupActivity.findViewWithTag("next_meeting_c");
                int dsa = no.getId();
                EditText dd = (EditText) no.findViewById(dsa);
                Editable current_date = dd.getText();
                dd.setText(date_selection);


            }
        }
    }




    private class DateListenerResched implements DatePickerDialog.OnDateSetListener {
        View mView;

        public DateListenerResched(View view) {
            mView = view;
        }

        public void onDateSet(DatePicker view, int year, int month, int monthDay) {

            String month_big = null;
            int month_plus = month + 1;

            String day_p = null;
            String sting_day = null;
            sting_day = String.valueOf(monthDay);

            if(monthDay < 10){

                day_p = '0'+sting_day;

            }
            else{
                day_p = sting_day;
            }

            if(!(month_plus >= 10)){

                month_big = "0"+ month_plus;
                String date_selection = year + "-" + month_big + "-" + day_p;

                if(MODE == Util.EDIT_MODE){
                    reScheduleMeeting(date_selection);
                }else{

                    String datetime_selection = date_selection + " 08:00:00";

                    RelativeLayout rewd = mParent;
                    View no = viewGroupActivity.findViewWithTag("date_start");
                    int dsa = no.getId();
                    EditText dd = (EditText) no.findViewById(dsa);
                    Editable current_date = dd.getText();
                    dd.setText(datetime_selection);

                }


            }else{

                String date_selection = year + "-" + month_plus + "-" + day_p;

                if(mSugarBeanId != null){
                    reScheduleMeeting(date_selection);
                }else{

                    String datetime_selection = date_selection + " 08:00:00";

                    View no = viewGroupActivity.findViewWithTag("date_start");
                    //View no = mParent.findViewWithTag("date_start");
                    int dsa = no.getId();
                    EditText dd = (EditText) no.findViewById(dsa);
                    Editable current_date = dd.getText();
                    dd.setText(datetime_selection);

                }
            }
        }
    }


    public void saveNextMeeting(){

        DatabaseOperations dbOp = new DatabaseOperations(context);
        boolean resultChild = dbOp.select_child_meetings(mSugarBeanId);

        if(resultChild == true){

            AutoCompleteTextView editText = (AutoCompleteTextView) (mDetailsTable.findViewWithTag("next_meeting_c"));

            String valueNextMeet = editText.getText().toString();

            if(valueNextMeet != null && !valueNextMeet.equals("null") && !valueNextMeet.isEmpty() && !valueNextMeet.equals("") && !valueNextMeet.equals(" ")){

                modifiedValues.put(SugarCRMContent.Meetings.START_DATE, valueNextMeet);
                modifiedValues.put(SugarCRMContent.Meetings.NEXT_MEETING_C, "");
                modifiedValues.put(SugarCRMContent.Meetings.PARENT_MEETING,  mSugarBeanId);
                modifiedValues.put(SugarCRMContent.Meetings.NAME,  "Следваща среща  ");
                modifiedValues.put(SugarCRMContent.Meetings.STATUS,  "Planned");
                modifiedValues.put(SugarCRMContent.Meetings.MEETING_MOL_C,  "no");
                modifiedValues.put(SugarCRMContent.Meetings.ACCOUNT_NAME,  mAccountName);
                modifiedValues.put(SugarCRMContent.Meetings.DELETED, "0");


                Object lead_id = hidden_fields.get("lead_id_c");
                Object account_id = hidden_fields.get("account_id_c");


                if(lead_id != null){

                    modifiedValues.put("lead_id_c", hidden_fields.get("lead_id_c").toString());
                    comeFromModule = "Leads";

                }else if(account_id != null){

                    modifiedValues.put("account_id_c", hidden_fields.get("account_id_c").toString());
                    comeFromModule = "Accounts";
                }

                String uniqueID = UUID.randomUUID().toString();
                modifiedValues.put("id", uniqueID);

                try{
                    dbOper.insert_new_record(modifiedValues, mModuleName, mSugarBeanId, "from_app", "Meetings");
                    Toast toast = Toast.makeText(context, "Създадена е следваща среща", Toast.LENGTH_LONG);
                    toast.show();
                }catch(Exception e){
                    Toast toast = Toast.makeText(context, "Грешка при създаването на следваща среща", Toast.LENGTH_LONG);
                    toast.show();

                }

                String company_module = null;

                value_meeting_mol = "second_automatic_meeting";
                save_question_record(uniqueID, comeFromModule, "additional_record");

            }else{

                Toast toast = Toast.makeText(context, "Липсва дата за следваща среща", Toast.LENGTH_LONG);
                toast.show();
            }
        }



    }


    public void saveModuleItem(View v, String mode) {

        mProgressDialog = ViewUtil.getProgressDialog(context, getString(R.string.saving), false);
        mProgressDialog.show();

        if ( MODE == Util.EDIT_MODE ){
            modifiedValues.put(RestUtilConstants.ID, mSugarBeanId);
        }

        int counter = 0;
        for ( ViewConfiguration.FieldConfiguration item : mSelectFields ) {

            if( item.edit_view == true ){

            String fieldName = item.name;

   //===================spiner start=====================================

            if(item.type == "option"){

                Spinner mySpinner = (Spinner) mDetailsTable.findViewWithTag(fieldName);
                String status_spinner = mySpinner.getSelectedItem().toString();
                String option_value = "";

                if(fieldName.equals("status")){

                    if(status_spinner.equals("Планирана")){

                        option_value = "Planned";

                    }else{

                        option_value = "Held";
                    }
                }
                else if(fieldName.equals("meeting_mol_c")){

                    if(status_spinner.equals("Да")){

                        option_value = "yes";
                    }else if (status_spinner.equals("Не")){

                        option_value = "no";
                    }else {

                        option_value = "";
                    }
                }
                modifiedValues.put(fieldName, option_value);
                counter++;
            }
//===================spiner end=====================================
            else if(item.type == "hidden_field"){

                Object pp = hidden_fields.get(fieldName);
                if(pp != null){

                    modifiedValues.put(fieldName, pp.toString());

                }

            }
            else{

                    AutoCompleteTextView editText = (AutoCompleteTextView) ((ViewGroup) mDetailsTable.getChildAt(counter)).getChildAt(1);
                    String fieldValue = editText.getText().toString();

                    //=====check for empty fields===new method==
                    if(fieldName.equals("name")){

                        if(TextUtils.isEmpty(fieldValue)){
                            hasError = true;
                            editText.setError(getString(R.string.emptyFieldsValidationMessage));
                        }
                    }else if(fieldName.equals("date_start")){

                        if(TextUtils.isEmpty(fieldValue)){
                            hasError = true;
                            editText.setError(getString(R.string.emptyFieldsValidationMessage));
                        }
                    }else if(fieldName.equals("eik_c")){

                        if(TextUtils.isEmpty(fieldValue)){
                            hasError = true;
                            editText.setError(getString(R.string.emptyFieldsValidationMessage));
                        }
                    }else if(fieldName.equals("phone_work")){

                        if(TextUtils.isEmpty(fieldValue)){
                            hasError = true;
                            editText.setError(getString(R.string.emptyFieldsValidationMessage));
                        }
                    }
                    //================================

                    modifiedValues.put(fieldName, editText.getText().toString());
                    counter++;
                }
            }

            String user_name = prefs.getString("username", "default");
            modifiedValues.put("assigned_user_name", user_name);

        }

        if (!hasError) {



            if (MODE == Util.EDIT_MODE) {

                dbOper.update_existing_record(modifiedValues, mModuleName, mSugarBeanId, mRowId);

            } else if (MODE == Util.NEW_MODE) {

                String uniqueID = UUID.randomUUID().toString();

                modifiedValues.put("id", uniqueID);
                modifiedValues.put(ModuleFields.DELETED, Util.NEW_ITEM);


                try {

                    if(mModuleName.equals("Meetings")){

                        dbOper.insert_new_record(modifiedValues, mModuleName, mSugarBeanId, "from_app", comeFromModule);
                        save_question_record(uniqueID, comeFromModule, "original_record");
                        saveNextMeeting();

                    }else{
                        dbOper.insert_new_record(modifiedValues, mModuleName, mSugarBeanId, "from_app", comeFromModule);

                    }
                    newRecordGuid = uniqueID;

                } catch (Exception e){

                    String ex = e.toString();

                }
            }

            ViewUtil.dismissVirtualKeyboard(context, v);
            finish();

//=======open detail view afther save========

                showDetailView();

//===============
        } else {
            hasError = false;

            mProgressDialog.cancel();

        }

    }

    public void showDetailView() {


        String quid = "";
        Object pp = hidden_fields.get("id");

        if(pp != null){

            quid = pp.toString();

        }else if(newRecordGuid != null){

            quid = newRecordGuid;

        }

        //String g = quid;

        ModuleListFragment listInstanse = new ModuleListFragment();
        String lastId = dbOper.selectLast(mModuleName);
        int lastIdInt = Integer.parseInt(lastId);

        Cursor cursor = dbOper.select_detail_view_data_by_guid(mModuleName, quid);

        if (cursor == null || cursor.getCount() <= 0) {
            return;
        }


        cursor.moveToFirst();
        Intent detailIntent = new Intent(context, ModuleDetailActivity.class);
        detailIntent.putExtra(Util.ROW_ID, cursor.getString(cursor.getColumnIndex("_id")));
        //detailIntent.putExtra(Util.ROW_ID, cursor.getString(0));
        detailIntent.putExtra(RestUtilConstants.BEAN_ID, cursor.getString(1));
        detailIntent.putExtra(RestUtilConstants.MODULE_NAME, mModuleName);
        detailIntent.putExtra("Relation", true);
        //if (ViewUtil.isTablet(context)) {
        // ((BaseMultiPaneActivity) getActivity()).openActivityOrFragment(detailIntent);
        // } else {
        startActivity(detailIntent);
        //}
    }



    private String count_meetings_customer(SQLiteDatabase db, String id_customer, String module_name){


        //field_name да махна c -то
        String query;
        String number = null;
        String field_name = module_name.substring(0, module_name.length() - 1).toLowerCase() +"_id_c";
        query = "SELECT COUNT (_id) as number FROM meetings WHERE '" +  field_name   + "'  =  '" +  id_customer  + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        number = cursor.getString(cursor.getColumnIndex("number"));


        return number;

    }


    public void save_question_record(String meeting_id, String company_module, String call_from) {
        String type_meeting_c = null;
        String uniqueID = UUID.randomUUID().toString();

        if(call_from.equals("additional_record")){

            if(company_module != null){

                if(company_module.equals("Accounts")){
                    type_meeting_c = "active_schedule";
                }else{
                    type_meeting_c = "potential_serial";
                }
            }else {


            }
        }

        else if(call_from.equals("original_record")){

            if(company_module.equals("Leads")){

                String number = count_meetings_customer(db, lead_id_c, "Leads");

                if(number.equals("0") || number.equals("") || number == null){


                    if(value_meeting_mol == null){
                        type_meeting_c = "potential_serial";
                    }else{

                        if(value_meeting_mol.equals("yes")){
                            type_meeting_c = "potential_first_mol";
                        }else if(value_meeting_mol.equals("no")){
                            type_meeting_c = "potential_first";

                        }else if(value_meeting_mol.equals("second_automatic_meeting")){

                            type_meeting_c = "potential_serial";

                        }else {
                            type_meeting_c = "potential_first";
                        }
                    }


                }else{

                    if(value_meeting_mol.equals("yes")){
                        type_meeting_c = "potential_serial_mol";
                    }else if(value_meeting_mol.equals("no")){
                        type_meeting_c = "potential_serial";
                    }else {
                        type_meeting_c = "potential_serial";
                    }
                }

            }else if(company_module.equals("Accounts")){

                type_meeting_c = "active_schedule";

            }

        }


        String insert_query_questions = "INSERT INTO q_questions ( meeting_id, id, created_in_app, exported, assigned_user_id, type_meeting_c ) VALUES ('" + meeting_id + "', '" + uniqueID  + "', 1, 0,  '" +  assignedUserSugarId  + "', '" +  type_meeting_c  + "');";


        db.execSQL(insert_query_questions);


    }

    public boolean reScheduleMeeting(String date_selection) {

        String datetime_selection = date_selection + " 08:00:00";


        View no = viewGroupActivity.findViewWithTag("date_start");

        int dsa = no.getId();
        EditText dd = (EditText) no.findViewById(dsa);
        Editable current_date = dd.getText();


        SQLiteDatabase dbq2 = databaseHelper.getWritableDatabase();

        String select_query1 = "SELECT resch_date1, resch_date2, resch_date3  FROM meetings WHERE _id =  ? ";

        String[] arguments = new String[1];
        arguments[0] = mRowId;

        Cursor cursor1 = dbq2.rawQuery(select_query1, arguments);
        String result1, result2, result3 = null;

        if(cursor1.moveToFirst()){

            result1 = cursor1.getString(cursor1.getColumnIndex("resch_date1"));
            result2 = cursor1.getString(cursor1.getColumnIndex("resch_date2"));
            result3 = cursor1.getString(cursor1.getColumnIndex("resch_date3"));


            if(result1 == null){
                dd.setText(datetime_selection);
                String update_query = "UPDATE meetings SET date_start = '" + current_date + "', resch_date1 = '" + current_date + "' WHERE _id = '" + mRowId + "'";
                dbq2.execSQL(update_query);
                return true;
            }
            else if(result2 == null){
                dd.setText(datetime_selection);
                String update_query = "UPDATE meetings SET date_start = '" + current_date + "', resch_date2 = '" + current_date + "' WHERE _id = '" + mRowId + "'";
                dbq2.execSQL(update_query);
                return true;
            }
            else if(result3 == null){
                dd.setText(datetime_selection);
                String update_query = "UPDATE meetings SET date_start = '" + current_date + "', resch_date3 = '" + current_date + "' WHERE _id = '" + mRowId + "'";
                dbq2.execSQL(update_query);
                return true;
            }else{

                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, "Срещата не може да се препланира повече от 3 пъти !", duration);
                toast.show();

            }

        }

        return true;
    }

    public void viewModuleList(String moduleName) {


        Intent myIntent;

        myIntent = new Intent(context, ModulesActivity.class);
        myIntent.putExtra(RestUtilConstants.START_FROM, "new_meeting");
        myIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
        startActivity(myIntent);

    }

    @Override
    public void onResume() {

        super.onResume();

        viewGroupActivity = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

    }


    public String buildDateString( int year_s, int month_s, int date_s, int hour_s, int min_s ) {

        String day_pp = "";
        String month_big = "";
        String date_sting_val = "";
        date_sting_val = String.valueOf(date_s);

        if(date_s < 10){

            day_pp = '0'+ date_sting_val;

        }
        else{
            day_pp = date_sting_val;
        }

        if(!(month_s >= 10)){
            month_big = "0"+ month_s;
        }

        String date_selection = year_s + "-" + month_big + "-" + day_pp;
        String hour_string = hour_s + ":" + min_s + ":00";
        String datetime_selection = date_selection + " " + hour_string;

        return datetime_selection;

    }
}
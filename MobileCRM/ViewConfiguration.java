package com.paraflow.mobilecrm;

import java.util.ArrayList;
import java.util.HashMap;

import static com.paraflow.mobilecrm.ModuleFields.MEETING_MOL_C;

/**
 * Created by tmihaylov on 20.2.2018 г..
 */

public class ViewConfiguration {



    public static FieldConfiguration[] meetings = new FieldConfiguration[13];//размера на масива трябва да е коректно въведен

    static {

        meetings[0] = new FieldConfiguration("_id", "hidden_field", null, true, true, "", false  );
        meetings[1] = new FieldConfiguration("id", "hidden_field", null, true, true, "bean_id", false );
        meetings[2] = new FieldConfiguration("name", "text", null, true, true, "Име", false );
        meetings[3] = new FieldConfiguration("account_name", "text", null, true, true, "Име на клиента", false );
        meetings[4] = new FieldConfiguration("assigned_user_id", "text", null, false, false, "Потребител", true );
        meetings[5] = new FieldConfiguration("deleted", "text", null, false, false, "Изтрит", false );
        meetings[6] = new FieldConfiguration("account_id_c", "hidden_field", null, true, true, "Акаунт ID", true );
        meetings[7] = new FieldConfiguration("lead_id_c", "hidden_field", null, true, true, "Потенциален клиент", true );
        meetings[8] = new FieldConfiguration("meeting_mol_c", "option",Options.yes_no , true, true, "Среща с МОЛ", false );
        meetings[9] = new FieldConfiguration("status", "option", Options.meeting_status , true, true, "Статус", false );
        meetings[10] = new FieldConfiguration("date_start", "date", null, true, true, "Дата старт", false );
        meetings[11] = new FieldConfiguration("next_meeting_c", "date", null, true, true, "Следваща среща", false );
        meetings[12] = new FieldConfiguration("description", "text", null, true, true, "Описание", false );

    }


    public static FieldConfiguration[] leads = new FieldConfiguration[9];//размера на масива трябва да е коректно въведен

    static {
        leads[0] = new FieldConfiguration("_id", "hidden_field", null, true, true, "", false  );
        leads[1] = new FieldConfiguration("id", "hidden_field", null, false, true, "id", false  );
        leads[2] = new FieldConfiguration("account_name", "text", null, true, true, "Име на клиента", false );
        leads[3] = new FieldConfiguration("assigned_user_id", "hidden_field", null, true, true, "Потребител", true );
        leads[4] = new FieldConfiguration("deleted", "hidden_field", null, false, true, "Изтрит", false );
        leads[5] = new FieldConfiguration("phone_work", "text", null, true, true, "Телефон", true );
        leads[6] = new FieldConfiguration("address", "text", null, true, true, "Адрес", true );
        leads[7] = new FieldConfiguration("lead_source", "text", null, true, true, "Източник", true );
        leads[8] = new FieldConfiguration("eik_c", "text", null, true, true, "ЕИК", true );

    }


    public static FieldConfiguration[] accounts = new FieldConfiguration[7];//размера на масива трябва да е коректно въведен

    static {

        accounts[0] = new FieldConfiguration("_id", "hidden_field", null, true, true, "", false  );
        accounts[1] = new FieldConfiguration("id", "hidden_field", null, false, true, "id", false  );
        accounts[2] = new FieldConfiguration("name", "text", null, true, true, "Име на клиента", false );
        //accounts[3] = new FieldConfiguration("assigned_user_id", "hidden_field", null, true, true, "Потребител", true );
        accounts[3] = new FieldConfiguration("deleted", "hidden_field", null, false, true, "Изтрит", false );
        accounts[4] = new FieldConfiguration("address_full_c", "text", null, true, true, "Адрес", false );
        accounts[5] = new FieldConfiguration("description", "text", null, true, true, "Описание", false );
        accounts[6] = new FieldConfiguration("mol_name_c", "text", null, true, true, "Име на МОЛ", false );

    }

    public static class FieldConfiguration {


        public String name = "";
        public String type = "";
        public boolean id_to_name = false;
        public String label = "";
        public ArrayList<String> options;
        public boolean edit_view = false;
        public boolean detail_view = false;


       public FieldConfiguration( String name_s, String type_s, ArrayList<String> options_s, boolean edit_view_s, boolean detail_view_s, String label_s, boolean id_to_name_s  ){

        this.name = name_s;
        this.type = type_s;
        this.options = options_s;
        this.edit_view = edit_view_s;
        this.detail_view = detail_view_s;
        this.label = label_s;
        this.id_to_name = id_to_name_s;

        }



    }

}

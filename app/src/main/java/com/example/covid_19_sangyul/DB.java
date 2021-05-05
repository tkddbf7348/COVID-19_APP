package com.example.covid_19_sangyul;

import android.provider.BaseColumns;

public final class DB {

    public static final class CreateDB implements BaseColumns {
        public static final String City = "City";
        public static final String stdDay = "stdDay";
        public static final String overFlowCnt = "overFlowCnt";
        public static final String localOccCnt = "localOccCnt";
        public static final String isolIngCnt = "isolIngCnt";
        public static final String defCnt = "defCnt";
        public static final String deathCnt = "deathCnt";
        public static final String TABLENAME = "COVID-19";

        public static final String _CREATE0 = "create table if not exists "+TABLENAME+"("
                +_ID+" integer primary key autoincrement, "
                +City+" text not null , "
                +stdDay+" text not null , "
                +overFlowCnt+" integer not null , "
                +localOccCnt+" integer not null , "
                +isolIngCnt+" integer not null , "
                +defCnt+" integer not null , "
                +deathCnt+" text not null );";
    }
}

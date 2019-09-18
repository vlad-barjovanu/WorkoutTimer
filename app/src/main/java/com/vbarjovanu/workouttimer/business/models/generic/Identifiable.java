package com.vbarjovanu.workouttimer.business.models.generic;

public interface Identifiable {
    String getPrimaryKey();

    void setPrimaryKey(String primaryKey);
}

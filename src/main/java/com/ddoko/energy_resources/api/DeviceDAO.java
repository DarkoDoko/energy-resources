package com.ddoko.energy_resources.api;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

public interface DeviceDAO {

    @SqlQuery("SELECT state FROM <table> WHERE UUID = :uuid")
    boolean getDeviceState(@Define("table") String table, @Bind("uuid") String uuid);

    @SqlQuery("INSERT INTO <table> (UUID, STATE) VALUES (:uuid, :charging)")
    void setDeviceState(@Define("table") String table, @Bind("uuid") String uuid, @Bind("charging") boolean charging);
}

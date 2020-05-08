package com.atlassian.migration.datacenter.spi;

public class MigrationReadyStatus
{
    private Boolean dbCompatible;
    private Boolean osCompatible;
    private Boolean fsSizeCompatible;

    public MigrationReadyStatus(Boolean dbCompatible, Boolean osCompatible, Boolean fsSizeCompatible)
    {
        this.dbCompatible = dbCompatible;
        this.osCompatible = osCompatible;
        this.fsSizeCompatible = fsSizeCompatible;
    }

    public Boolean getDbCompatible()
    {
        return dbCompatible;
    }

    public Boolean getOsCompatible()
    {
        return osCompatible;
    }

    public Boolean getFsSizeCompatible()
    {
        return fsSizeCompatible;
    }
}

package com.umut.ssh.filesystem;

public abstract class FileProperties {

    protected String name;
    protected Directory parent = null;


    public FileProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected void setParentDirectory(Directory d) {
        parent = d;
    }

    public Directory getParentDirectory() {
        return parent;
    }

    @Override
    public String toString() {
        return name;
    }
}

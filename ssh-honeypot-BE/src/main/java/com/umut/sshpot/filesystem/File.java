package com.umut.sshpot.filesystem;



public class File extends FileProperties {

    public File(String name) {
        super(name);
    }

    public boolean exists() {
        return false;
    }
}
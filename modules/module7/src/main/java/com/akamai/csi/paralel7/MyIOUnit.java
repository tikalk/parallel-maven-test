package com.akamai.csi.paralel7;

import java.util.List;
import java.util.*;
import java.io.*;

public class MyIOUnit {

    protected List<String> tokens = new ArrayList<String>();

    public void read(InputStream input) throws IOException {
        StringBuilder builder = new StringBuilder();

        int data = input.read();
        while(data != -1){
            if(((char)data) != ','){
                builder.append((char) data);
            } else {
                this.tokens.add(builder.toString().trim());
                builder.delete(0, builder.length());
            }

            data = input.read();
        }
    }

    public void write(OutputStream output) throws IOException {
        for(int i=0; i<tokens.size(); i++){
            if(i>0){
                output.write(',');
            }
            output.write(tokens.get(i).getBytes());
        }
    }
}

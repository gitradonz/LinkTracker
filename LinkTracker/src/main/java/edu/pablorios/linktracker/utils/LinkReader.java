package edu.pablorios.linktracker.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class LinkReader extends BufferedReader {
    
    public LinkReader(Reader reader) {
        super(reader);
    }
    
    private String searchLinks(String line) {
        if(line == null) return line;
        
        boolean finished = false;
        int begin, end = -1;
        StringJoiner strJoin = new StringJoiner("\n");
        
        while(!finished) {
            begin = line.indexOf("<a", end + 1);
            if(begin == -1)
                finished = true;
            else {
                end = line.indexOf("</a>", begin + 1);
                if(end == -1) finished = true;
                else strJoin.add(line.substring(begin, end + 4));
            }
        }
        
        return strJoin.length() == 0?null:strJoin.toString();
    }
    
    @Override
    public String readLine() throws IOException {
        String line;
        String result = null;
        while(result == null && (line = super.readLine()) != null) {
            result = searchLinks(line);
        }
        
        return result;
    }
    
    private static String getCharset(String contentType) {
        if (contentType != null) {
            for (String param : contentType.replace(" ", "").split(";")) {
                if (param.startsWith("charset=")) {
                    return param.split("=", 2)[1];
                }
            }
        }
        
        return null; // Probably binary content
    }  

    public static List<String> getLinks (String urlToParse) {

        LinkReader bufInput = null;
        List<String> result = new ArrayList<>();
        
        try {
            URL url  = new URL(urlToParse);
            URLConnection conn = url.openConnection();
            String charset = getCharset(conn.getHeaderField("Content-Type"));
            if (charset != null)
                bufInput = new LinkReader(
                    new InputStreamReader(conn.getInputStream(), charset));
            else
                bufInput = new LinkReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = bufInput.readLine()) != null) {
                
                result.addAll(Arrays.asList(line.split("\n")));
            }
        } catch (IOException ex) { }
        
        return result;
    }

}

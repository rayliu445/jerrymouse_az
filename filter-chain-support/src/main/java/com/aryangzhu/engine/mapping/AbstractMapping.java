package com.aryangzhu.engine.mapping;

import java.util.regex.Pattern;

public class AbstractMapping implements Comparable<AbstractMapping>{

    final Pattern pattern;

    final String url;

    public AbstractMapping(String url) {
        this.url = url;
        this.pattern = buildPattern(url);
    }

    public boolean matches(String uri){
        return pattern.matcher(uri).matches();
    }

    Pattern buildPattern(String url){
        StringBuilder sb = new StringBuilder(url.length() + 16);
        sb.append('^');
        for(int i=0;i<url.length();i++){
            char ch = url.charAt(i);
            if(ch=='*'){
                sb.append(".*");
            }else if(ch>='a' && ch<='z' || ch>='A' && ch<='Z' || ch>='0' && ch<='9'){
                sb.append(ch);
            }else{
                sb.append('\\').append(ch);
            }
        }
        sb.append('$');
        return Pattern.compile(sb.toString());
    }

    @Override
    public int compareTo(AbstractMapping o) {
        int cmp = this.priority() - o.priority();
        if (cmp == 0) {
            cmp = this.url.compareTo(o.url);
        }
        return cmp;
    }

    int priority(){
        if(this.url.equals("/")){
            return Integer.MAX_VALUE;
        }
        if(this.url.startsWith("*")){
            return Integer.MAX_VALUE-1;
        }

        return 100000-this.url.length();
    }
}

package transactions;

import javax.swing.*;
import java.util.*;
public class Util {

    public static void println(String s){
        if (Symbols.debugFlag) {
            System.out.println(s);
            System.out.flush();
        }
    }

    public static void println(String s,JTextArea textArea){
        if (Symbols.debugFlag) {
            textArea.append(s + "\n");
        }
    }
}

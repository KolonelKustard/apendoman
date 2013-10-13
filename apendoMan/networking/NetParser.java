/*
 * ParseOutput.java
 *
 * Created on 09 October 2000, 15:21
    *
    *This class encodes and decodes information sent to and from the server...
    *In theory there shouldn't need to be any error checking either.
    *However I may add some if I get time (to stop the server falling over).
 */

package apendoMan.networking;

import java.util.*;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class NetParser extends java.lang.Object {
    
    private String out;
    private String in;
    
    private Vector params = new Vector();
    private Vector params2 = new Vector();
    
    //private String stringOut;
    private String stringIn;
    //private float floatOut;
    private float floatIn;
    //private int intOut;
    private int intIn;
    
    public static final String PLAIN_TEXT = "Text";
    public static final String USER_ID = "UserID";
    public static final String GAME_START = "Start";
    public static final String CLIENT_NUM = "ClientNum";
    
    public static final String TORSO_MAT = "TorsoMat";
    public static final String HEAD_MAT = "HeadMat";
    public static final String LEFT_HAND_MAT = "LHandMat";
    public static final String RIGHT_HAND_MAT = "RHandMat";
    public static final String LEFT_FOOT_MAT = "LFootMat";
    public static final String RIGHT_FOOT_MAT = "RFootMat";
    
    public static final String TG_MAT = "TGMat";
    public static final String TG_MAT_LT = "TGl";
    
    public void NetParser () {
        out = "";
        in = "";
    }
    
    
    
    // Methods follow that deal with output...
    
    public void newOutput (String s) {
        out = "&";
        out += s;
        out += "~";
    }
    
    public void newOutput (String s, String s2) {
        out = "&";
        out += s;
        out += "~";
        out += parseText (s2);
    }
    
    public void addOutputCommand (String s) {
        if (out.length() > 0) {
            out += "|";
        }
        out += "&";
        out += s;
        out += "~";
    }
    
    public void addOutputParam (String s) {
        if (out.charAt(out.length()-1) != '~') {
            out += ",";
        }
        out += parseText(s);
    }
    
    public void addOutputParam (float f) {
        if (out.charAt(out.length()-1) != '~') {
            out += ",";
        }
        out += f;
    }
    
    public void addOutputParam (int f) {
        if (out.charAt(out.length()-1) != '~') {
            out += ",";
        }
        out += f;
    }
    
    public String getOutput () {
        if (out.length() > 0) {
            if (out.charAt(out.length()-1) != '|') {
                out += "|";
            }
            return out;
        }
        else
            return null;
    }
    
    
    // Methods follow for dealing with input...
    
    public void newInput (String s) {
        params = parseInput(s);
    }
    
    public int getInputSize () {
        return params.size();
    }
    
    public int getNumOfParams (int commandNumber) {
        params2 = (Vector)params.elementAt(commandNumber);
        return params2.size() - 1;
    }
    
    public String getInputCommand (int commandNumber) {
        params2 = (Vector)params.elementAt(commandNumber);
        stringIn = (String)params2.elementAt(0);
        return stringIn;
    }
    
    // Get the string from the parameter.
    public String getInputParamString (int commandNumber, int paramNumber) {
        params2 = (Vector)params.elementAt(commandNumber);
        stringIn = (String)params2.elementAt(paramNumber + 1);  // Have to add 1 to param number else you'd get the command instead.
        return stringIn;
    }
    
    // Get the float from the parameter.  Will fail if param is not a float...
    public float getInputParamFloat (int commandNumber, int paramNumber) {
        params2 = (Vector)params.elementAt(commandNumber);
        stringIn = (String)params2.elementAt(paramNumber + 1);
        floatIn = Float.parseFloat(stringIn);
        return floatIn;
    }
    
    public int getInputParamInt (int commandNumber, int paramNumber) {
        params2 = (Vector)params.elementAt(commandNumber);
        stringIn = (String)params2.elementAt(paramNumber + 1);
        intIn = Integer.parseInt(stringIn);
        return intIn;
    }
    
    public void reset () {
        in = "";
        out = "";
        params.removeAllElements();
    }
        
        
        
    //  Here follows the static methods for encryption and decryption...
    
    private static String parseText (String s) {
        String s2 = "";
        
        for (int num = 0; num < s.length(); num++) {
            switch (s.charAt(num)) {
                case '|' :
                    s2 = s2 + "*|*";
                    break;
                
                case '&' :
                    s2 = s2 + "*&*";
                    break;
                
                case '~' :
                    s2 = s2 + "*~*";
                    break;
                
                case ',' :
                    s2 = s2 + "*,*";
                    break;
                
                case '*' :
                    s2 = s2 + "***";
                    break;
                    
                default :
                    s2 = s2 + s.charAt(num);
                    break;
            }
        }
        
        return s2;
    }

    private static Vector parseInput (String s) {
        Vector v = new Vector();
        Vector v2 = new Vector();
        String currentString = "";
        int param = 1;
        int command = 0;
        
        for (int num = 0; num < s.length(); num++) {           
            switch (s.charAt(num)) {
                case '&' :
                    currentString = "";
                    break;
                
                case '~' :
                    param = 1;
                    v2.add(0, currentString);
                    currentString = "";
                    break;
                
                case ',' :
                    v2.add(param, currentString);
                    currentString = "";
                    param++;
                    break;
                    
                case '|' :
                    v2.add(param, currentString);
                    v.add(command, v2);
                    v2 = new Vector();
                    command++;
                    break;
                
                case '*' :
                    num++;
                    currentString = currentString + s.charAt(num);
                    num++;
                    break;
                    
                default :
                    currentString = currentString + s.charAt(num);
                    break;
            }
        }
        
        return v;
    }

}

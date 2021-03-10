 package com.gmrz.fido2.utils;
 
 import android.util.Log;

 public final class Logger
 {
   public static final int LOG_IS_SWITCHED_OFF = 8;
   public static final int LOG_IS_SWITCHED_ON = 1;
   public static final int LOG_MAX_LEN = 4000;
   private static boolean logEnabled = true;
 
   public static boolean setLogEnabled(boolean aLogEnabled)
   {
     boolean temp = logEnabled;
     logEnabled = aLogEnabled;
     return temp;
   }
 
   public static int i(String tag, String msg)
   {
     int level = 4;
     if (!logEnabled) {
       return 8;
     }
     int remainder = msg.length();
     if (remainder <= 4000) {
       Log.i(tag, msg);
     } else {
       int start = 0;
       int end = 0;
       do {
         end += (remainder > 4000 ? 4000 : remainder);
         remainder -= end - start;
         Log.i(tag, msg.substring(start, end));
         start += 4000;
       }while (remainder > 0);
     }
     return level;
   }
 
   public static int i(String tag, Throwable tr)
   {
     return i(tag, tr.toString());
   }
 
   public static int i(String tag, String msg, byte[] buffer)
   {
     if (!logEnabled) {
       return 8;
     }
     return i(tag, dumpBytes(msg, buffer));
   }
 
   public static int v(String tag, String msg)
   {
     int level = 2;
     if (!logEnabled) {
       return 8;
     }
     int remainder = msg.length();
     if (remainder <= 4000) {
       Log.v(tag, msg);
     } else {
       int start = 0;
       int end = 0;
       do {
         end += (remainder > 4000 ? 4000 : remainder);
         remainder -= end - start;
         Log.v(tag, msg.substring(start, end));
         start += 4000;
       }while (remainder > 0);
     }
     return level;
   }
 
   public static int v(String tag, Throwable tr)
   {
     return v(tag, tr.toString());
   }
 
   public static int v(String tag, String msg, byte[] buffer)
   {
     if (!logEnabled) {
       return 8;
     }
     return v(tag, dumpBytes(msg, buffer));
   }
 
   public static int w(String tag, String msg)
   {
     int level = 5;
     if (!logEnabled) {
       return 8;
     }
     int remainder = msg.length();
     if (remainder <= 4000) {
       Log.w(tag, msg);
     } else {
       int start = 0;
       int end = 0;
       do {
         end += (remainder > 4000 ? 4000 : remainder);
         remainder -= end - start;
         Log.w(tag, msg.substring(start, end));
         start += 4000;
       }while (remainder > 0);
     }
 
     return level;
   }
 
   public static int w(String tag, Throwable tr)
   {
     return w(tag, tr.toString());
   }
 
   public static int w(String tag, String msg, byte[] buffer)
   {
     if (!logEnabled) {
       return 8;
     }
     return w(tag, dumpBytes(msg, buffer));
   }
 
   public static int d(String tag, String msg)
   {
     int level = 3;
     if (!logEnabled) {
       return 8;
     }
     int remainder = msg.length();
     if (remainder <= 4000) {
       Log.d(tag, msg);
     } else {
       int start = 0;
       int end = 0;
       do {
         end += (remainder > 4000 ? 4000 : remainder);
         remainder -= end - start;
         Log.d(tag, msg.substring(start, end));
         start += 4000;
       }while (remainder > 0);
     }
 
     return level;
   }
 
   public static int d(String tag, Throwable tr)
   {
     return d(tag, tr.toString());
   }
 
   public static int d(String tag, String msg, byte[] buffer)
   {
     if (!logEnabled) {
       return 8;
     }
     return Log.d(tag, dumpBytes(msg, buffer));
   }
 
   public static int e(String tag, String msg)
   {
     int level = 6;
     if (!logEnabled) {
       return 8;
     }
     int remainder = msg.length();
     if (remainder <= 4000) {
       Log.e(tag, msg);
     } else {
       int start = 0;
       int end = 0;
       do {
         end += (remainder > 4000 ? 4000 : remainder);
         remainder -= end - start;
         Log.e(tag, msg.substring(start, end));
         start += 4000;
       }while (remainder > 0);
     }
 
     return level;
   }
 
   public static int e(String tag, Throwable tr)
   {
     if (!logEnabled) {
       return 8;
     }
     return Log.e(tag, "", tr);
   }
 
   public static int e(String tag, String message, Throwable tr)
   {
     if (!logEnabled) {
       return 8;
     }
     return Log.e(tag, message, tr);
   }
 
   public static int e(String tag, String msg, byte[] buffer)
   {
     if (!logEnabled) {
       return 8;
     }
     return e(tag, dumpBytes(msg, buffer));
   }
 
   private static String dumpBytes(String title, byte[] bytes)
   {
     String text = "";
 
     if (title != null) {
       text = text + title;
     }
 
     if (bytes == null) {
       text = text + ":null";
       return text;
     }
 
     int lineLength = 16;
     int inLentgh = bytes.length;
 
     text = text + "(" + inLentgh + "):\n";
 
     for (int i = 0; i < inLentgh; i += 16)
     {
       text = text + String.format("%06x:", Integer.valueOf(i));
       for (int j = 0; j < 16; j++) {
         if (i + j < inLentgh) {
           text = text + String.format("%02x ", Integer.valueOf(bytes[(i + j)] & 0xFF));
         }
         else {
           text = text + "   ";
         }
       }
 
       text = text + " ";
       for (int j = 0; j < 16; j++) {
         if (i + j < inLentgh) {
           char ch = (char)(bytes[(i + j)] & 0xFF);
           text = text + String.format("%c", Character.valueOf((ch >= ' ') && (ch <= '~') ? ch : '.'));
         }
       }
       text = text + "\n";
     }
     return text;
   }
 }

 
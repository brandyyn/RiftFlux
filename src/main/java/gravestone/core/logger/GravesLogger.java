package gravestone.core.logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;

public class GravesLogger extends AbstractLogger {
   private static File logFile;
   private static final String LOG_FILE_DIRECTORY = "logs/";
   private static final String LOG_FILE_NAME = "graveLogs.log";
   private static final DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

   public static void setWorldDirectory(File worldDirectory) {
      File logsDir = new File(worldDirectory, "logs/");
      logsDir.mkdir();
      StringBuilder fileName = new StringBuilder();
      fileName.append("logs/").append(FILE_DATE_FORMAT.format(new Date())).append(" ").append("graveLogs.log");
      logFile = new File(worldDirectory, fileName.toString());
   }

   protected boolean isEnabled(Level level, Marker marker, Message data, Throwable t) {
      return true;
   }

   protected boolean isEnabled(Level level, Marker marker, Object data, Throwable t) {
      return true;
   }

   protected boolean isEnabled(Level level, Marker marker, String data) {
      return true;
   }

   protected boolean isEnabled(Level level, Marker marker, String data, Object... p1) {
      return true;
   }

   protected boolean isEnabled(Level level, Marker marker, String data, Throwable t) {
      return true;
   }

   public void log(Marker marker, String fqcn, Level level, Message msg, Throwable throwable) {
      if (logFile != null) {
         StringBuilder loggedStr = new StringBuilder();
         loggedStr.append(DATE_FORMAT.format(new Date()));
         loggedStr.append(" [");
         loggedStr.append(level.toString());
         loggedStr.append("] ");
         loggedStr.append(msg.getFormattedMessage());
         Object[] params = msg.getParameters();
         Throwable t;
         if (throwable == null && params != null && params[params.length - 1] instanceof Throwable) {
            t = (Throwable)params[params.length - 1];
         } else {
            t = throwable;
         }

         if (t != null) {
            loggedStr.append(" ");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(baos));
            loggedStr.append(baos.toString());
         }

         try {
            PrintWriter out = new PrintWriter(new FileWriter(logFile, true), true);
            out.println(loggedStr.toString());
         } catch (IOException var10) {
            GSLogger.logError("Error while writing in graves log file!");
            var10.printStackTrace();
         }
      } else {
         GSLogger.logError("Graves logs file doesn't exists");
      }

   }
}

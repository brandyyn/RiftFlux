package com.voidsrift.riftflux.vortex.lib.helper;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IOHelper {
   private static final Gson gson = (new GsonBuilder()).enableComplexMapKeySerialization().setPrettyPrinting().create();

   public static void createDirectory(File outputDirectory, String sourceDirectory, List<String> files) {
      if (!outputDirectory.exists()) {
         outputDirectory.mkdirs();
         if (sourceDirectory != null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            for(int i = 0; i < files.size(); ++i) {
               String file = (String)files.get(i);

               try {
                  final InputStream inputStream = loader.getResourceAsStream(sourceDirectory + "/" + file);
                  Throwable var7 = null;

                  try {
                     (new ByteSource() {
                        public InputStream openStream() throws IOException {
                           return inputStream;
                        }
                     }).copyTo(Files.asByteSink(new File(outputDirectory + "/" + file)));
                  } catch (Throwable var17) {
                     var7 = var17;
                     throw var17;
                  } finally {
                     if (inputStream != null) {
                        if (var7 != null) {
                           try {
                              inputStream.close();
                           } catch (Throwable var16) {
                              var7.addSuppressed(var16);
                           }
                        } else {
                           inputStream.close();
                        }
                     }

                  }
               } catch (IOException var19) {
                  var19.printStackTrace();
               }
            }
         }
      }

   }

   public static void createDirectory(File outputDirectory) {
      createDirectory(outputDirectory, (String)null, (List)null);
   }

   public static <T> ArrayList<T> readJsonFiles(Class<T> classImport, File configDirectory) {
      File[] files = configDirectory.listFiles();
      ArrayList<T> jsonObjects = new ArrayList();

      for(int i = 0; i < files.length; ++i) {
         File file = files[i];
         if (Files.getFileExtension(file.getName()).equals("json")) {
            try {
               JsonReader jsonReader = new JsonReader(new FileReader(file));
               jsonObjects.add(gson.fromJson((JsonReader)jsonReader, (Type)classImport));
            } catch (JsonSyntaxException | IOException var7) {
               LogHelper.fatal("The file " + file.getName() + " is an invalid JSON and could not be loaded.");
               throw new IllegalArgumentException("Unable to load " + file.getName() + ". Verify validity.", var7);
            }
         }
      }

      return jsonObjects;
   }
}

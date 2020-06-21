package net.fiendishplatypus.utils.fileindex.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Objects;

public class RecipesExampleDemo {

  public static void main(String[] args) throws URISyntaxException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    String fileName = "recipes-example.xml";
    URI is = Objects.requireNonNull(classloader.getResource(fileName), "File not found: " + fileName).toURI();
    File file = Paths.get(is).toFile();
    String endRecordMarker = "    </Property>";
    Indexer indexer = new Indexer(file, "GcRefinerRecipe.xml", endRecordMarker);
    ArrayDeque<Record> recipeIdToRecordStart = indexer.buildIndex();

    System.out.println("Index table:");

    try {
      RandomAccessFile raf = new RandomAccessFile(file, "r");

      for (Record record : recipeIdToRecordStart) {
        System.out.println(record);
        raf.seek(record.start);
        ByteBuffer buffer = ByteBuffer.allocate(record.sizeInt());
        raf.getChannel().read(buffer);
        buffer.flip();
        System.out.println(new String(buffer.array()));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}

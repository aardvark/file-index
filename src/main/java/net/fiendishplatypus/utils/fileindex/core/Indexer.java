package net.fiendishplatypus.utils.fileindex.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Indexer {
  private static final Logger log = LoggerFactory.getLogger(Indexer.class);

  private final File file;
  private final AtomicBoolean searchIdFlag = new AtomicBoolean(false);
  private final AtomicLong idx = new AtomicLong();
  private final ArrayDeque<Record> records = new ArrayDeque<>();
  private final ArrayDeque<Long> idxs = new ArrayDeque<>(2);
  private final String startMark;
  private final String endMark;

  public Indexer(File file, String startMark, String endMark) {
    this.file = file;

    this.startMark = startMark;
    this.endMark = endMark;

    idxs.add(0L);
    idxs.add(0L);
  }

  public ArrayDeque<Record> buildIndex() {
    try (Stream<String> stream = Files.lines(file.toPath())) {
      stream.forEach(s -> {
        int lineSizeInBytes = (s + "\n").getBytes().length + 1;

        log.debug("{}\t| {}\t | {}", lineSizeInBytes, idx.longValue(), s);

        if (s.contains(startMark)) {
          searchIdFlag.set(true);
        }

        if (s.contains("name=\"Id\"") && searchIdFlag.get()) {
          String startPhrase = "value=\"";
          int indexOf = s.indexOf(startPhrase);
          int valueStart = indexOf + startPhrase.length();
          String id = s.substring(valueStart, s.indexOf('"', valueStart));
          idxs.pop();
          Record record = new Record(id, idxs.peek());
          records.addFirst(record);

          searchIdFlag.set(false);
        }

        if (s.equals(endMark)) {
          long endMarkerIdx = idx.longValue();
          Record currentRecord = records.pop();

          log.debug("Populate record: {}", currentRecord.id);
          log.debug("Start record: {}", currentRecord.start);
          log.debug("End marker: {}", endMarkerIdx);
          log.debug("Section size: {}", (endMarkerIdx - currentRecord.start));

          Record record = new Record(currentRecord, endMarkerIdx);
          records.add(record);
        }

        idxs.push(idx.accumulateAndGet(lineSizeInBytes, Long::sum));
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return records;
  }
}

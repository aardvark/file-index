package net.fiendishplatypus.utils.fileindex.core;

public class Record {
  final String id;
  final Long start;
  final Long end;


  Record(String id, Long start) {
    this.id = id;
    this.start = start;
    this.end = 0L;
  }

  Record(net.fiendishplatypus.utils.fileindex.core.Record record, Long end) {
    this.id = record.id;
    this.start = record.start;
    this.end = end;
  }

  long size() {
    return end - start;
  }

  int sizeInt() {
    return ((int) (end - start));
  }

  @Override
  public String toString() {
    return "Record{" + "id='" + id + '\'' + " [ " + start + ", " + end + "]}";
  }
}

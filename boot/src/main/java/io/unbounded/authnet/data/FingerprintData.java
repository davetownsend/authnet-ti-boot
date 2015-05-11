package io.unbounded.authnet.data;

public class FingerprintData {

  long sequence;  // x_fp_sequence
  long timestamp; // x_fp_timestamp
  String hash;    //x_fp_hash

  public FingerprintData(long sequence, long timestamp, String hash) {
    this.sequence = sequence;
    this.timestamp = timestamp;
    this.hash = hash;
  }

  public long getSequence() {
    return sequence;
  }

  public void setSequence(long sequence) {
    this.sequence = sequence;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }
}
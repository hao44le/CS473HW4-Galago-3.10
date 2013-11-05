/*
 *  BSD License (http://lemurproject.org/galago-license)
 */
package org.lemurproject.galago.utility.compression.integer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author sjh
 */
public class SignedVByteReader implements CompressedLongReader {

  long currentOffset = 0;
  DataInputStream stream;

  public SignedVByteReader(InputStream stream) {
    this.stream = new DataInputStream(stream);
  }

  @Override
  public int readInt() throws IOException {
    long v = readLong();
    assert (v <= Integer.MAX_VALUE);
    return (int) v;
  }

  @Override
  public long readLong() throws IOException {
    long result = 0;
    long b;

    for (int position = 0; true; position++) {
      assert position < 10;
      b = stream.read();
      currentOffset += 1;

      if ((b & 0x80) == 0x80) {
        result |= ((long) (b & 0x3f) << (7 * position));

        if ((b & 0x40) != 0) {
          result *= -1;
        }

        break;
      } else {

        result |= (long) (b << (7 * position));
      }
    }

    return result;
  }

  @Override
  public boolean seek(long absoluteOffset) throws IOException {
    long relativeOffset = (absoluteOffset - currentOffset);
    if (relativeOffset > 0) {
      // same as skip -- but actually works //
      while(currentOffset < absoluteOffset){
        stream.read();
        currentOffset+=1;
      }
      return true;
    }
    return false;
  }
}

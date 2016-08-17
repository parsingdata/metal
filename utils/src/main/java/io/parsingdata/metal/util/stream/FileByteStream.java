/*
 * Copyright 2013-2016 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal.util.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import io.parsingdata.metal.data.ByteStream;

/**
 * ByteStream implementation based on RandomAccessFile.
 *
 * @author Netherlands Forensic Institute.
 */
public final class FileByteStream implements ByteStream, AutoCloseable {

    private final RandomAccessFile _randomAccessFile;
    private long _length;

    /**
     * Construct a new FileByteStream used for reading from a file.
     *
     * @param file the file to read from
     */
    public FileByteStream(final File file) {
        try {
            _randomAccessFile = new RandomAccessFile(file, "r");
            _length = _randomAccessFile.length();
        }
        catch (final FileNotFoundException e) {
            throw new IllegalArgumentException("file could not be found", e);
        }
        catch (final IOException e) {
            throw new IllegalArgumentException("could not determine file length", e);
        }
    }

    @Override
    public int read(final long offset, final byte[] buffer) throws IOException {
        if (offset + buffer.length > getSize()) {
            return 0;
        }
        _randomAccessFile.seek(offset);
        _randomAccessFile.readFully(buffer);
        return buffer.length;
    }

    @Override
    public void close() {
        try {
            _randomAccessFile.close();
        }
        catch (final IOException e) {
            throw new IllegalStateException("could not close file", e);
        }
    }

    public long getSize() throws IOException {
        return _length;
    }
}
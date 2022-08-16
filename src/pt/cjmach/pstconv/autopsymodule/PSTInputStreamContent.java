/*
 *  Copyright 2022 Carlos Machado
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package pt.cjmach.pstconv.autopsymodule;

import com.pff.PSTFileContent;
import java.io.IOException;
import org.sleuthkit.datamodel.ReadContentInputStream;

/**
 *
 * @author cmachado
 */
public class PSTInputStreamContent extends PSTFileContent {
    private final ReadContentInputStream stream;

    public PSTInputStreamContent(ReadContentInputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("stream is null.");
        }
        this.stream = stream;
    }

    @Override
    public void seek(long l) throws IOException {
        stream.seek(l);
    }

    @Override
    public long getFilePointer() throws IOException {
        return stream.getCurPosition();
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return stream.read(bytes);
    }

    @Override
    public byte readByte() throws IOException {
        byte[] buffer = new byte[1];
        return stream.read(buffer) > 0 ? buffer[0] : -1;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
    
}

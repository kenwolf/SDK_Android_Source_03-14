/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Default inoperative implementation of javax.net.ssl.SSLSocketFactory
 * 
 * @since Android 1.0
 */
class DefaultSSLSocketFactory extends SSLSocketFactory {
    
    private String errMessage;
    
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    /**
     * @see javax.net.ssl.SSLSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
     */
    public Socket createSocket(Socket s, String host, int port,
            boolean autoClose) throws IOException {
        throw new SocketException(errMessage);
    }

    /**
     * @see javax.net.SocketFactory#createSocket(java.lang.String, int)
     */
    public Socket createSocket(String host, int port) throws IOException,
            UnknownHostException {
        throw new SocketException(errMessage);
    }

    /**
     * @see javax.net.SocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int)
     */
    public Socket createSocket(String host, int port, InetAddress localHost,
            int localPort) throws IOException, UnknownHostException {
        throw new SocketException(errMessage);
    }

    /**
     * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int)
     */
    public Socket createSocket(InetAddress host, int port) throws IOException {
        throw new SocketException(errMessage);
    }

    /**
     * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int, java.net.InetAddress, int)
     */
    public Socket createSocket(InetAddress address, int port,
            InetAddress localAddress, int localPort) throws IOException {
        throw new SocketException(errMessage);
    }
    
    DefaultSSLSocketFactory(String mes) {
        errMessage = mes;
    }

}

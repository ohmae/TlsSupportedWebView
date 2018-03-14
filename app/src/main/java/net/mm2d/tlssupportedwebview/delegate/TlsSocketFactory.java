/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.tlssupportedwebview.delegate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
class TlsSocketFactory extends SSLSocketFactory {
    private static final String[] TLS = {"TLSv1.1", "TLSv1.2"};
    private final SSLSocketFactory mDelegate;

    TlsSocketFactory(final SSLSocketFactory factory) {
        mDelegate = factory;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return mDelegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return mDelegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(
            final Socket s,
            final String host,
            final int port,
            final boolean autoClose) throws IOException {
        return setProtocols(mDelegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(
            final String host,
            final int port) throws IOException {
        return setProtocols(mDelegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(
            final String host,
            final int port,
            final InetAddress localHost,
            final int localPort) throws IOException {
        return setProtocols(mDelegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(
            final InetAddress host,
            final int port) throws IOException {
        return setProtocols(mDelegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(
            final InetAddress address,
            final int port,
            final InetAddress localAddress,
            final int localPort) throws IOException {
        return setProtocols(mDelegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket setProtocols(final Socket socket) {
        if (socket instanceof SSLSocket) {
            ((SSLSocket) socket).setEnabledProtocols(TLS);
        }
        return socket;
    }
}

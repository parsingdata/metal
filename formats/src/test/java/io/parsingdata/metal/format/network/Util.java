package io.parsingdata.metal.format.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public final class Util {

    private Util() {
    }

    /**
     * Convert a hexadecimal String to a byte array.
     *
     * It does this by converting each two characters to the byte they represent, e.g.:
     *
     *     "E5D34F" = E5 D3 4F = [-27, -45, 79]
     *
     * @param hexString the string in hex format
     * @return a byte array containing the bytes represented by the String characters
     * @throws IllegalArgumentException thrown if an odd number or illegal characters are supplied
     */
    public static byte[] hexStringToBytes(final String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        }
        catch (final DecoderException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Convert an array of bytes representing an IPv4/6 address to a dotted decimal or colonized format respectively.
     *
     * @param ipBytes the bytes containing the IP data
     * @return a string representation of IPv4/6
     */
    public static String ipStringFromBytes(final byte[] ipBytes) {
        try {
            return InetAddress.getByAddress(ipBytes).getHostAddress();
        }
        catch (final UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Convert an integer representing an IPv4 address to its dotted decimal format.
     *
     * @param ip the int containing the IP data
     * @return a dotted decimal IPv4 String
     */
    public static String ipv4StringFromInt(final int ip) {
        return ipStringFromBytes(ByteBuffer.allocate(4).putInt(ip).array());
    }
}

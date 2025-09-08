package com.quectel.multicamera.utils;

public class ChannelDecoder {
    public static class Channel {
        public int res;
        public int type;

        public Channel(int res, int type) {
            this.res = res;
            this.type = type;
        }

        @Override
        public String toString() {
            return "res=" + res + ", type=" + type;
        }
    }

    public static Channel[] decode(int result) {
        Channel[] channels = new Channel[4];

        // Calculate ch3_res and ch3_type
        int ch3Value = result / 216;
        channels[3] = new Channel(ch3Value / 2, ch3Value % 2);

        // Calculate ch2_res and ch2_type
        result -= ch3Value * 216;
        int ch2Value = result / 36;
        channels[2] = new Channel(ch2Value / 2, ch2Value % 2);

        // Calculate ch1_res and ch1_type
        result -= ch2Value * 36;
        int ch1Value = result / 6;
        channels[1] = new Channel(ch1Value / 2, ch1Value % 2);

        // Calculate ch0_res and ch0_type
        result -= ch1Value * 6;
        int ch0Value = result;
        channels[0] = new Channel(ch0Value / 2, ch0Value % 2);

        return channels;
    }
}

package com.cleevio.vexl.module.group.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QrCodeUtil {

    public static int generateQRCode() {
        Random rnd = new Random();
        return rnd.nextInt(999999);
    }
}

/*
* Copyright (C) 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ca.alexland.renewpass.utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import ca.alexland.renewpass.exceptions.DecryptionFailedException;
import ca.alexland.renewpass.exceptions.EncryptionFailedException;

/**
 * Taken from https://developer.android.com/samples/BasicAndroidKeyStore and
 * https://github.com/obaro/SimpleKeystoreApp with modifications by Alex Land
 */
public class KeyStoreUtil {
    private String mAlias;

    public KeyStoreUtil(String alias) {
        this.mAlias = alias;
    }

    public boolean createKeys(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Create a start and end time, for the validity range of the key pair that's about to be
            // generated.
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 1);


            // The KeyPairGeneratorSpec object is how parameters for your key pair are passed
            // to the KeyPairGenerator.  For a fun home game, count how many classes in this sample
            // start with the phrase "KeyPair".
            KeyPairGeneratorSpec spec =
                new KeyPairGeneratorSpec.Builder(context)
                    // You'll use the alias later to retrieve the key.  It's a key for the key!
                    .setAlias(mAlias)
                            // The subject used for the self-signed certificate of the generated pair
                    .setSubject(new X500Principal("CN=" + mAlias))
                            // The serial number used for the self-signed certificate of the
                            // generated pair.
                    .setSerialNumber(BigInteger.valueOf(1337))
                            // Date range of validity for the generated pair.
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();

            // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
            // and the KeyStore.  This example uses the AndroidKeyStore.
            try {
                KeyPairGenerator kpGenerator = KeyPairGenerator
                        .getInstance(SecurityConstants.TYPE_RSA, SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                kpGenerator.initialize(spec);
                kpGenerator.generateKeyPair();
                return true;
            }
            catch(Exception e) {
                LoggerUtil.appendLogWithStacktrace(context, "Key generation failed: ", e);
                return false;
            }
        }
        else {
            return false;
        }

    }

    public String encryptPassword(String plainTextPassword) throws EncryptionFailedException {
        try {
            KeyStore keyStore = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyStore.load(null);

            KeyStore.Entry entry = keyStore.getEntry(mAlias, null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)entry;
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher input;
            input = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(plainTextPassword.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);
        }
        catch(Exception e) {
            throw new EncryptionFailedException(e);
        }
    }

    public String decryptPassword(String encryptedPassword) throws DecryptionFailedException {
        try {
            KeyStore keyStore = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(mAlias, null);

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(encryptedPassword, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }

            return new String(bytes, 0, bytes.length, "UTF-8");

        } catch (Exception e) {
            throw new DecryptionFailedException(e);
        }
    }

    public void setAlias(String alias) {
        mAlias = alias;
    }

    public boolean keysExist() {
        KeyStore.Entry entry = null;
        try {
            KeyStore keyStore = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyStore.load(null);
            entry = keyStore.getEntry(mAlias, null);
        } catch (Exception e) {
            return false;
        }
        return entry != null;
    }
}

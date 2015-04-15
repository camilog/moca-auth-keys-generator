package com.threepartballot;

import paillierp.key.KeyGen;
import paillierp.key.PaillierKey;
import paillierp.key.PaillierPrivateThresholdKey;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

public class GenerateKeys {

    public static void saveToFile(String fileName, PaillierKey value) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        try{
            oout.writeObject(value);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }

    public static void saveToFile(String fileName, BigInteger value) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        try{
            oout.writeObject(value);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }

    static public void main(String[] args) throws IOException {
        SecureRandom r = new SecureRandom();

        System.out.println("Bienvenida(o) a la generación de claves para la autoridad de la votación\n");
        System.out.println("NOTA: La generación de la clave privada se realiza a través de criptografía umbral");
        System.out.print("Ingrese el numero de partes (autoridades) en que se dividirá la clave privada: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        System.out.print("Ingrese el numero minimo de partes (autoridades) que son necesarias para revelar la clave privada: ");
        int k = Integer.parseInt(br.readLine());

        // Private Key Files.
        File dir2 = new File("partsOfPrivateKey");
        dir2.mkdir();
        PaillierPrivateThresholdKey[] keys = KeyGen.PaillierThresholdKey(256, n, k, r.nextInt());
        for (int i = 0; i < keys.length; i++){
            saveToFile("partsOfPrivateKey/privateKeyPart" + i + ".key", keys[i]);
        }

        // Public Key File
        File dir1 = new File("publicValue");
        dir1.mkdir();
        saveToFile("publicValue/publicKeyN.key", keys[0].getPublicKey().getN());

        System.out.println("\nRepartir valores publicos guardados en publicValues/");
        System.out.println("Repartir partes de la clave privada entre las distintas autoridades, guardados en partsOfPrivateKey/");
        System.out.println("\nProceso finalizado exitosamente.");

    }

}

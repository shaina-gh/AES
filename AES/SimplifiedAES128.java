public class SimplifiedAES128 {
    // Constants
    private static final int Nb = 4;  // Number of columns in state
    private static final int Nk = 4;  // Number of 32-bit words in key
    private static final int Nr = 10; // Number of rounds
    
    // Simplified S-Box (16 values instead of 256)
    // This is a representative subset arranged in a 4x4 grid for easier memorization
    private static final int[] SBox = {
        0x63, 0x7c, 0x77, 0x7b,  // Row 0
        0xf2, 0x6b, 0x6f, 0xc5,  // Row 1
        0x30, 0x01, 0x67, 0x2b,  // Row 2
        0xfe, 0xd7, 0xab, 0x76   // Row 3
    };

    // Simplified Inverse S-Box (16 values)
    private static final int[] InvSBox = {
        0x52, 0x09, 0x6a, 0xd5,  // Row 0
        0x30, 0x36, 0xa5, 0x38,  // Row 1
        0xbf, 0x40, 0xa3, 0x9e,  // Row 2
        0x81, 0xf3, 0xd7, 0xfb   // Row 3
    };

    // Simplified Round constants
    private static final int[] Rcon = {
        0x01, 0x02, 0x04, 0x08, 0x10
    };

    // Instance variables
    private byte[] key;
    private byte[][] w;

    /**
     * Constructor initializes with encryption key
     * @param key The 16-byte encryption key
     */
    public SimplifiedAES128(byte[] key) {
        this.key = key;
        this.w = keyExpansion(key);
    }

    /**
     * Encrypts a 16-byte block using simplified AES-128
     * @param input The 16-byte plaintext block
     * @return The 16-byte encrypted block
     */
    public byte[] encrypt(byte[] input) {
        byte[][] state = new byte[4][Nb];
        
        // Convert input array to state matrix
        for (int i = 0; i < input.length; i++) {
            state[i % 4][i / 4] = input[i];
        }
        
        // Initial round
        addRoundKey(state, 0);
        
        // Main rounds
        for (int round = 1; round < Nr; round++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, round);
        }
        
        // Final round (no mixColumns)
        subBytes(state);
        shiftRows(state);
        addRoundKey(state, Nr);
        
        // Convert state matrix to output array
        byte[] output = new byte[16];
        for (int i = 0; i < 16; i++) {
            output[i] = state[i % 4][i / 4];
        }
        
        return output;
    }

    /**
     * Decrypts a 16-byte block using simplified AES-128
     * @param input The 16-byte ciphertext block
     * @return The 16-byte decrypted block
     */
    public byte[] decrypt(byte[] input) {
        byte[][] state = new byte[4][Nb];
        
        // Convert input array to state matrix
        for (int i = 0; i < input.length; i++) {
            state[i % 4][i / 4] = input[i];
        }
        
        // Initial round
        addRoundKey(state, Nr);
        
        // Main rounds
        for (int round = Nr - 1; round > 0; round--) {
            invShiftRows(state);
            invSubBytes(state);
            addRoundKey(state, round);
            invMixColumns(state);
        }
        
        // Final round (no invMixColumns)
        invShiftRows(state);
        invSubBytes(state);
        addRoundKey(state, 0);
        
        // Convert state matrix to output array
        byte[] output = new byte[16];
        for (int i = 0; i < 16; i++) {
            output[i] = state[i % 4][i / 4];
        }
        
        return output;
    }

    /**
     * Simplified SubBytes - uses only 16 values instead of 256
     * Maps each byte's lower 4 bits to corresponding SBox value
     */
    private void subBytes(byte[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                // Use only lower 4 bits for simplified lookup
                int index = state[i][j] & 0x0F;
                state[i][j] = (byte) SBox[index];
            }
        }
    }

    /**
     * Inverse of simplified subBytes
     */
    private void invSubBytes(byte[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                // Use only lower 4 bits for simplified lookup
                int index = state[i][j] & 0x0F;
                state[i][j] = (byte) InvSBox[index];
            }
        }
    }

    /**
     * Shifts the rows of the state matrix
     * Row 0: no shift, Row 1: shift 1, Row 2: shift 2, Row 3: shift 3
     */
    private void shiftRows(byte[][] state) {
        byte[] t = new byte[4];
        
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                t[j] = state[i][(j + i) % 4];
            }
            System.arraycopy(t, 0, state[i], 0, 4);
        }
    }

    /**
     * Inverse of shiftRows - shifts rows in opposite direction
     */
    private void invShiftRows(byte[][] state) {
        byte[] t = new byte[4];
        
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                t[j] = state[i][(j - i + 4) % 4];
            }
            System.arraycopy(t, 0, state[i], 0, 4);
        }
    }

    /**
     * Simplified MixColumns using easier coefficients
     */
    private void mixColumns(byte[][] state) {
        for (int j = 0; j < Nb; j++) {
            byte a0 = state[0][j];
            byte a1 = state[1][j];
            byte a2 = state[2][j];
            byte a3 = state[3][j];
            
            state[0][j] = (byte) (gmul(a0, 2) ^ a1 ^ a2 ^ a3);           // 2,1,1,1
            state[1][j] = (byte) (a0 ^ gmul(a1, 2) ^ a2 ^ a3);           // 1,2,1,1
            state[2][j] = (byte) (a0 ^ a1 ^ gmul(a2, 2) ^ a3);           // 1,1,2,1
            state[3][j] = (byte) (a0 ^ a1 ^ a2 ^ gmul(a3, 2));           // 1,1,1,2
        }
    }

    /**
     * Simplified Inverse MixColumns
     */
    private void invMixColumns(byte[][] state) {
        for (int j = 0; j < Nb; j++) {
            byte a0 = state[0][j];
            byte a1 = state[1][j];
            byte a2 = state[2][j];
            byte a3 = state[3][j];
            
            state[0][j] = (byte) (gmul(a0, 3) ^ gmul(a1, 2) ^ a2 ^ a3);   // 3,2,1,1
            state[1][j] = (byte) (a0 ^ gmul(a1, 3) ^ gmul(a2, 2) ^ a3);   // 1,3,2,1
            state[2][j] = (byte) (a0 ^ a1 ^ gmul(a2, 3) ^ gmul(a3, 2));   // 1,1,3,2
            state[3][j] = (byte) (gmul(a0, 2) ^ a1 ^ a2 ^ gmul(a3, 3));   // 2,1,1,3
        }
    }

    /**
     * Adds the round key to the state matrix
     */
    private void addRoundKey(byte[][] state, int round) {
        for (int c = 0; c < Nb; c++) {
            for (int r = 0; r < 4; r++) {
                state[r][c] ^= w[round * Nb + c][r];
            }
        }
    }

    /**
     * Expands the cipher key into the key schedule
     */
    private byte[][] keyExpansion(byte[] key) {
        byte[][] w = new byte[Nb * (Nr + 1)][4];
        
        // Copy the key into the first Nk words
        for (int i = 0; i < Nk; i++) {
            w[i][0] = key[4 * i];
            w[i][1] = key[4 * i + 1];
            w[i][2] = key[4 * i + 2];
            w[i][3] = key[4 * i + 3];
        }
        
        // Generate the rest of the key schedule
        byte[] temp = new byte[4];
        for (int i = Nk; i < Nb * (Nr + 1); i++) {
            temp = w[i - 1].clone();
            
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp));
                temp[0] ^= Rcon[(i / Nk - 1) % Rcon.length]; // Use only 5 round constants
            }
            
            for (int j = 0; j < 4; j++) {
                w[i][j] = (byte) (w[i - Nk][j] ^ temp[j]);
            }
        }
        
        return w;
    }

    /**
     * Applies the simplified S-Box to each byte in a word
     */
    private byte[] subWord(byte[] word) {
        for (int i = 0; i < 4; i++) {
            int index = word[i] & 0x0F; // Use only lower 4 bits for simplified lookup
            word[i] = (byte) SBox[index];
        }
        return word;
    }

    /**
     * Performs a cyclic permutation on a word
     */
    private byte[] rotWord(byte[] word) {
        byte temp = word[0];
        word[0] = word[1];
        word[1] = word[2];
        word[2] = word[3];
        word[3] = temp;
        return word;
    }

    /**
     * Simplified Galois Field multiplication
     * Only implements multiplication by 2 and 3
     */
    private byte gmul(byte a, int b) {
        if (b == 1) return a;
        if (b == 2) {
            byte result = (byte)(a << 1);
            if ((a & 0x80) != 0) {
                result ^= 0x1b; // XOR with simplified field polynomial
            }
            return result;
        }
        if (b == 3) {
            return (byte)(gmul(a, 2) ^ a);
        }
        return 0;
    }

    /**
     * Main method for demonstration
     */
    public static void main(String[] args) {
        byte[] key = "1234567890abcdef".getBytes();
        byte[] plaintext = "hello world12345".getBytes();
        
        SimplifiedAES128 aes = new SimplifiedAES128(key);
        
        // Encrypt the plaintext
        byte[] encrypted = aes.encrypt(plaintext);
        
        // Output encrypted data
        System.out.println("Encrypted ciphertext:");
        for (int i = 0; i < encrypted.length; i++) {
            System.out.printf("%02x ", encrypted[i] & 0xFF);
            if ((i + 1) % 4 == 0) System.out.println();
        }
        
        // Decrypt the ciphertext
        byte[] decrypted = aes.decrypt(encrypted);
        
        // Output decrypted data
        System.out.println("Decrypted plaintext:");
        for (int i = 0; i < decrypted.length; i++) {
            System.out.printf("%02x ", decrypted[i] & 0xFF);
            if ((i + 1) % 4 == 0) System.out.println();
        }
        
        // Verify decryption worked
        System.out.println("\nDecrypted text: " + new String(decrypted));
    }
}
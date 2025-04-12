# 🔐 Simplified AES-128 Implementation in Java

### 📌 Author
**Shaina**  

---

## 🧠 What is AES?

The **Advanced Encryption Standard (AES)** is a symmetric block cipher used worldwide for secure data encryption. It operates on 128-bit blocks with key sizes of 128, 192, or 256 bits.

### AES Core Operations:
- **SubBytes** – Byte substitution using an S-Box
- **ShiftRows** – Cyclically shift rows in the state
- **MixColumns** – Mix data within each column using matrix multiplication
- **AddRoundKey** – XOR the state with a round-specific key

---

## 🎯 Objective

This project demonstrates a **simplified AES-128 encryption/decryption algorithm** in Java, using:
- A reduced S-Box (16 entries for learning simplicity)
- Basic MixColumns and Galois Field logic
- Full key expansion for 10 rounds

---

## 🛠️ Features

- 128-bit block and key size
- 10 Rounds of encryption and decryption
- Custom simplified:
  - S-Box / Inverse S-Box
  - MixColumns logic
- Includes both encryption and decryption flows
- Console output with verification

---

## 🧪 Encryption/Decryption Procedure

### Encryption
1. **Key Expansion**
2. **Initial AddRoundKey**
3. **Rounds 1-9**
   - SubBytes → ShiftRows → MixColumns → AddRoundKey
4. **Final Round (10)**
   - SubBytes → ShiftRows → AddRoundKey

### Decryption
1. **Initial AddRoundKey**
2. **Rounds 9-1**
   - InvShiftRows → InvSubBytes → AddRoundKey → InvMixColumns
3. **Final Round**
   - InvShiftRows → InvSubBytes → AddRoundKey

---

## ▶️ How to Compile & Run

```bash
javac SimplifiedAES128.java
java SimplifiedAES128
```

---

## 🍴 How to Fork This Repository

Want to use or contribute to this project? Follow the steps below to fork and work with your own copy.

### 🔗 Repository URL
**https://github.com/shaina-gh/AES**

---

### 🔄 Step-by-Step Instructions

1. **Login to GitHub** and go to the repo: [shaina-gh/AES](https://github.com/shaina-gh/AES)
2. Click the **"Fork"** button at the top-right corner.
3. GitHub will create a **copy under your own account**.

---

### 🧪 After Forking – Get Started Locally

```bash
# 1. Clone your forked repository
git clone https://github.com/YOUR-USERNAME/AES.git

# 2. Navigate into the project directory
cd AES

# 3. Compile and run the project
javac SimplifiedAES128.java
java SimplifiedAES128
```





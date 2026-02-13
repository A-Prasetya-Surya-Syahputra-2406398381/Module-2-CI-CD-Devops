### Refleksi 1

Clean code dalam source code:

- Menerapkan nama variable dan method yang memberi konteks yang jelas (Meaningful Name)
- Method pada Controller hanya melakukan satu tugas (Single Responsibility)

Secure Coding:
- Menerapkan input validasi saat update product

Bisa lebih lanjut improve source code dengan menerapkan secure coding seperti Authentication, Authorization, Output data Encoding, dan Input data validation

### Refleksi 2

Tidak ada angka pasti untuk jumlah unit test, prinsip utamanya adalah menguji seluruh alur logika. Unit testing bisa dirasa cukup jika mayoritas skenario alur logika sudah di uji dengan melihat persentase coverage testing.

Isu yang mungkin bangkit dari pembuatan test tersebut adalah:

- Code duplication, jika mengulang logika setup yang sama pada beberapa kelas membuat kode sulit dipelihara apabila terdapat perubahan di set upnya

Improvisasi yang mungkin dilakukan adalah membuat sebuah base class yang berisi setup common dan test lain hanya melakukan extend ke class tersebut
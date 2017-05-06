final byte HEADER = 0x80
final byte FOOTER = 0x81

int packetCount = 0;
int pos = 0;
int size = 0;

File base = new File('.')
File outDir = new File(base, 'build')
outDir.mkdirs()

new File(base, 'src/data/受信データ.dump').withInputStream {input->

    def out = new File(outDir, "${packetCount}.out").newOutputStream();

    byte b;
    while((b = input.read()) != -1) {
        out.write(b);
        switch (pos++) {
        case (0..1):
            if (b != HEADER)
                pos = 0;
            break;

        case 2:
            size = ((int)b) << 8;
            break;

        case 3:
            size += b;
            break;

        case (4..<(4+size)):
            break;

        case 4+size:
            if (b == FOOTER) {
                break;
            }

        // 終端がずれている場合はfall throughして即座に次のファイルへ
        case 4+size+1:
            out.close();
            out = new File(outDir, "${++packetCount}.out").newOutputStream();
            pos = size = 0;
            break;
        }
    }
}

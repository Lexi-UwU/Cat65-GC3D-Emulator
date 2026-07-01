package org.kittykat.cat65.core.cpu;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kittykat.cat65.Cat65;
import org.kittykat.cat65.EmuHelper;
import org.kittykat.cat65.core.CMU;
import org.kittykat.cat65.ui.window.WindowWithTitle;

// ToDo: update to emulate the 65c02 instead
/**
 * Note: this is an emulator for the 6502 <b>not</b> the 65<u>C</u>02<br>
 * the 65C02 would have a few more instructions, and it forces all unused opcodes to be the same as $ea (NOP)
 **/
public class CPU extends WindowWithTitle {
    private final OpcodeType[] opcodeTypes = {
            new OpcodeType(this::ADC, new OpcodeDef[]{
                new OpcodeDef(this::value_imm,  2, new int[]{0x69}, null),
                new OpcodeDef(this::value_zp,   3, new int[]{0x65}, null),
                new OpcodeDef(this::value_zpX,  4, new int[]{0x75}, null),
                new OpcodeDef(this::value_abs,  4, new int[]{0x6d}, null),
                new OpcodeDef(this::value_absX, 4, new int[]{0x7d}, null),
                new OpcodeDef(this::value_absY, 4, new int[]{0x79}, null),
                new OpcodeDef(this::value_xInd, 6, new int[]{0x61}, null),
                new OpcodeDef(this::value_indY, 5, new int[]{0x71}, null),
            }),
            new OpcodeType(this::AND, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm,  2, new int[]{0x29}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0x25}, null),
                    new OpcodeDef(this::value_zpX,  4, new int[]{0x35}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0x2d}, null),
                    new OpcodeDef(this::value_absX, 4, new int[]{0x3d}, null),
                    new OpcodeDef(this::value_absY, 4, new int[]{0x39}, null),
                    new OpcodeDef(this::value_xInd, 6, new int[]{0x21}, null),
                    new OpcodeDef(this::value_indY, 5, new int[]{0x31}, null),
            }),
            new OpcodeType(this::ASL, new OpcodeDef[]{
                    new OpcodeDef(null,         2, new int[]{0x0a}, null),
                    new OpcodeDef(this::address_zp,   5, new int[]{0x06}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0x16}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0x0e}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0x1e}, null),
            }),
            new OpcodeType(this::B__, new OpcodeDef[]{
                    new OpcodeDef(null, 2, new int[]{0x10}, "NC"),  // BPL
                    new OpcodeDef(null, 2, new int[]{0x30}, "NS"),  // BMI
                    new OpcodeDef(null, 2, new int[]{0x50}, "VC"),  // BVC
                    new OpcodeDef(null, 2, new int[]{0x70}, "VS"),  // BVS
                    new OpcodeDef(null, 2, new int[]{0x90}, "CC"),  // BCC
                    new OpcodeDef(null, 2, new int[]{0xb0}, "CS"),  // BCS
                    new OpcodeDef(null, 2, new int[]{0xd0}, "ZC"),  // BNE
                    new OpcodeDef(null, 2, new int[]{0xf0}, "ZS"),  // BEQ
            }),
            new OpcodeType(this::BIT, new OpcodeDef[]{
                    new OpcodeDef(this::value_zp,  3, new int[]{0x24}, null),
                    new OpcodeDef(this::value_abs, 4, new int[]{0x2c}, null),
            }),
            new OpcodeType(this::BRK, new OpcodeDef[]{
                    new OpcodeDef(null, 7, new int[]{0x00}, null),
            }),
            new OpcodeType(this::CMP, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm,  2, new int[]{0xc9}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0xc5}, null),
                    new OpcodeDef(this::value_zpX,  4, new int[]{0xd5}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0xcd}, null),
                    new OpcodeDef(this::value_absX, 4, new int[]{0xdd}, null),
                    new OpcodeDef(this::value_absY, 4, new int[]{0xd9}, null),
                    new OpcodeDef(this::value_xInd, 6, new int[]{0xc1}, null),
                    new OpcodeDef(this::value_indY, 5, new int[]{0xd1}, null),
            }),
            new OpcodeType(this::CPX, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm, 2, new int[]{0xe0}, null),
                    new OpcodeDef(this::value_zp,  3, new int[]{0xe4}, null),
                    new OpcodeDef(this::value_abs, 4, new int[]{0xec}, null),
            }),
            new OpcodeType(this::CPY, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm, 2, new int[]{0xc0}, null),
                    new OpcodeDef(this::value_zp,  3, new int[]{0xc4}, null),
                    new OpcodeDef(this::value_abs, 4, new int[]{0xcc}, null),
            }),
            new OpcodeType(this::DEC, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   5, new int[]{0xc6}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0xd6}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0xce}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0xde}, null),
            }),
            new OpcodeType(this::DEX, new OpcodeDef[]{
                    new OpcodeDef(null, 2, new int[]{0xca}, null),
            }),
            new OpcodeType(this::DEY, new OpcodeDef[]{
                    new OpcodeDef(null, 2, new int[]{0x88}, null),
            }),
            new OpcodeType(this::EOR, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm,  2, new int[]{0x49}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0x45}, null),
                    new OpcodeDef(this::value_zpX,  4, new int[]{0x55}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0x4d}, null),
                    new OpcodeDef(this::value_absX, 4, new int[]{0x5d}, null),
                    new OpcodeDef(this::value_absY, 4, new int[]{0x59}, null),
                    new OpcodeDef(this::value_xInd, 6, new int[]{0x41}, null),
                    new OpcodeDef(this::value_indY, 5, new int[]{0x51}, null),
            }),
            new OpcodeType(this::SE_, new OpcodeDef[]{
                    new OpcodeDef(null, 2, new int[]{0x38}, "C"),  // SEC
                    new OpcodeDef(null, 2, new int[]{0x78}, "I"),  // SEI
                    new OpcodeDef(null, 2, new int[]{0xf8}, "D"),  // SED
            }),
            new OpcodeType(this::CL_, new OpcodeDef[]{
                    new OpcodeDef(null, 2, new int[]{0x18}, "C"),  // CLC
                    new OpcodeDef(null, 2, new int[]{0x58}, "I"),  // CLI
                    new OpcodeDef(null, 2, new int[]{0xb8}, "V"),  // CLV
                    new OpcodeDef(null, 2, new int[]{0xd8}, "D"),  // CLD
            }),
            new OpcodeType(this::INC, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   5, new int[]{0xe6}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0xf6}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0xee}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0xfe}, null),
            }),
            new OpcodeType(this::INX, new OpcodeDef[]{
                    new OpcodeDef(null, 2, new int[]{0xe8}, null),
            }),
            new OpcodeType(this::INY, new OpcodeDef[]{
                    new OpcodeDef(null, 2, new int[]{0xc8}, null),
            }),
            new OpcodeType(this::JMP, new OpcodeDef[]{
                    new OpcodeDef(this::address_abs, 3, new int[]{0x4c}, null),
                    new OpcodeDef(this::address_ind, 5, new int[]{0x6c}, null),
            }),
            new OpcodeType(this::JSR, new OpcodeDef[]{
                    new OpcodeDef(this::address_abs, 6, new int[]{0x20}, null),
            }),
            new OpcodeType(this::LDA, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm,  2, new int[]{0xa9}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0xa5}, null),
                    new OpcodeDef(this::value_zpX,  4, new int[]{0xb5}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0xad}, null),
                    new OpcodeDef(this::value_absX, 4, new int[]{0xbd}, null),
                    new OpcodeDef(this::value_absY, 4, new int[]{0xb9}, null),
                    new OpcodeDef(this::value_xInd, 6, new int[]{0xa1}, null),
                    new OpcodeDef(this::value_indY, 5, new int[]{0xb1}, null),
            }),
            new OpcodeType(this::LDX, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm,  2, new int[]{0xa2}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0xa6}, null),
                    new OpcodeDef(this::value_zpY,  4, new int[]{0xb6}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0xae}, null),
                    new OpcodeDef(this::value_absY, 4, new int[]{0xbe}, null),
            }),
            new OpcodeType(this::LDY, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm,  2, new int[]{0xa0}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0xa4}, null),
                    new OpcodeDef(this::value_zpX,  4, new int[]{0xb4}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0xac}, null),
                    new OpcodeDef(this::value_absX, 4, new int[]{0xbc}, null),
            }),
            new OpcodeType(this::LSR, new OpcodeDef[]{
                    new OpcodeDef(null,         2, new int[]{0x4a}, null),
                    new OpcodeDef(this::address_zp,   5, new int[]{0x46}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0x56}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0x4e}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0x5e}, null),
            }),
            new OpcodeType(this::NOP, new OpcodeDef[]{
                    new OpcodeDef(null,       2, new int[]{0x1a, 0x3a, 0x5a, 0x7a, 0xda, 0xea, 0xfa}, null),
                    new OpcodeDef(this::value_imm,  2, new int[]{0x80, 0x82, 0x89, 0xc2, 0xe2}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0x04, 0x44, 0x64}, null),
                    new OpcodeDef(this::value_zpX,  4, new int[]{0x14, 0x34, 0x54, 0x74, 0xd4, 0xf4}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0x0c}, null),
                    new OpcodeDef(this::value_absX, 4, new int[]{0x1c, 0x3c, 0x5c, 0x7c, 0xdc, 0xfc}, null),
            }),
            new OpcodeType(this::ORA, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm,  2, new int[]{0x09}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0x05}, null),
                    new OpcodeDef(this::value_zpX,  4, new int[]{0x15}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0x0d}, null),
                    new OpcodeDef(this::value_absX, 4, new int[]{0x1d}, null),
                    new OpcodeDef(this::value_absY, 4, new int[]{0x19}, null),
                    new OpcodeDef(this::value_xInd, 6, new int[]{0x01}, null),
                    new OpcodeDef(this::value_indY, 5, new int[]{0x11}, null),
            }),
            new OpcodeType(this::PH_, new OpcodeDef[]{
                    new OpcodeDef(null, 3, new int[]{0x48}, "A"),  // PHA
                    new OpcodeDef(null, 3, new int[]{0x08}, "P"),  // PHP
            }),
            new OpcodeType(this::PL_, new OpcodeDef[]{
                    new OpcodeDef(null, 4, new int[]{0x68}, "A"),  // PLA
                    new OpcodeDef(null, 4, new int[]{0x28}, "P"),  // PLP
            }),
            new OpcodeType(this::T__, new OpcodeDef[]{
                    new OpcodeDef(null, 2, new int[]{0xaa}, "AX"),  // TAX
                    new OpcodeDef(null, 2, new int[]{0x8a}, "XA"),  // TXA
                    new OpcodeDef(null, 2, new int[]{0xa8}, "AY"),  // TAY
                    new OpcodeDef(null, 2, new int[]{0x98}, "YA"),  // TYA
                    new OpcodeDef(null, 2, new int[]{0x9a}, "XS"),  // TXS
                    new OpcodeDef(null, 2, new int[]{0xba}, "SX"),  // TSX
            }),
            new OpcodeType(this::ROL, new OpcodeDef[]{
                    new OpcodeDef(null,         2, new int[]{0x2a}, null),
                    new OpcodeDef(this::address_zp,   5, new int[]{0x26}, null),
                    new OpcodeDef(this::address_zpX,  5, new int[]{0x36}, null),
                    new OpcodeDef(this::address_abs,  5, new int[]{0x2e}, null),
                    new OpcodeDef(this::address_absX, 5, new int[]{0x3e}, null),
            }),
            new OpcodeType(this::ROR, new OpcodeDef[]{
                    new OpcodeDef(null,         2, new int[]{0x6a}, null),
                    new OpcodeDef(this::address_zp,   5, new int[]{0x66}, null),
                    new OpcodeDef(this::address_zpX,  5, new int[]{0x76}, null),
                    new OpcodeDef(this::address_abs,  5, new int[]{0x6e}, null),
                    new OpcodeDef(this::address_absX, 5, new int[]{0x7e}, null),
            }),
            new OpcodeType(this::RTI, new OpcodeDef[]{
                    new OpcodeDef(null, 6, new int[]{0x40}, null),
            }),
            new OpcodeType(this::RTS, new OpcodeDef[]{
                    new OpcodeDef(null, 6, new int[]{0x60}, null),
            }),
            new OpcodeType(this::SBC, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm,  2, new int[]{0xe9, 0xeb}, null),
                    new OpcodeDef(this::value_zp,   3, new int[]{0xe5}, null),
                    new OpcodeDef(this::value_zpX,  4, new int[]{0xf5}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0xed}, null),
                    new OpcodeDef(this::value_absX, 4, new int[]{0xfd}, null),
                    new OpcodeDef(this::value_absY, 4, new int[]{0xf9}, null),
                    new OpcodeDef(this::value_xInd, 6, new int[]{0xe1}, null),
                    new OpcodeDef(this::value_indY, 5, new int[]{0xf1}, null),
            }),
            new OpcodeType(this::STA, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   3, new int[]{0x85}, null),
                    new OpcodeDef(this::address_zpX,  4, new int[]{0x95}, null),
                    new OpcodeDef(this::address_abs,  4, new int[]{0x8d}, null),
                    new OpcodeDef(this::address_absX, 4, new int[]{0x9d}, null),
                    new OpcodeDef(this::address_absY, 4, new int[]{0x99}, null),
                    new OpcodeDef(this::address_xInd, 6, new int[]{0x81}, null),
                    new OpcodeDef(this::address_indY, 5, new int[]{0x91}, null),
            }),
            new OpcodeType(this::STX, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,  3, new int[]{0x86}, null),
                    new OpcodeDef(this::address_zpY, 4, new int[]{0x96}, null),
                    new OpcodeDef(this::address_abs, 4, new int[]{0x8e}, null),
            }),
            new OpcodeType(this::STY, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,  3, new int[]{0x84}, null),
                    new OpcodeDef(this::address_zpX, 4, new int[]{0x94}, null),
                    new OpcodeDef(this::address_abs, 4, new int[]{0x8c}, null),
            }),

            // Illegal Opcodes
            new OpcodeType(this::ALR, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm, 2, new int[]{0x4b}, null),
            }),
            new OpcodeType(this::ANC, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm, 2, new int[]{0x0b, 0x2b}, null),
            }),
            new OpcodeType(this::ANE, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm, 2, new int[]{0x8b}, null),
            }),
            new OpcodeType(this::ARR, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm, 2, new int[]{0x6b}, null),
            }),
            new OpcodeType(this::DCP, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   5, new int[]{0xc7}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0xd7}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0xcf}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0xdf}, null),
                    new OpcodeDef(this::address_absY, 7, new int[]{0xdb}, null),
                    new OpcodeDef(this::address_xInd, 8, new int[]{0xc3}, null),
                    new OpcodeDef(this::address_indY, 8, new int[]{0xd3}, null),
            }),
            new OpcodeType(this::ISC, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   5, new int[]{0xe7}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0xf7}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0xef}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0xff}, null),
                    new OpcodeDef(this::address_absY, 7, new int[]{0xfb}, null),
                    new OpcodeDef(this::address_xInd, 8, new int[]{0xe3}, null),
                    new OpcodeDef(this::address_indY, 8, new int[]{0xf3}, null),
            }),
            new OpcodeType(this::LAS, new OpcodeDef[]{
                    new OpcodeDef(this::value_absY, 4, new int[]{0xbb}, null),
            }),
            new OpcodeType(this::LAX, new OpcodeDef[]{
                    new OpcodeDef(this::value_zp,   3, new int[]{0xa7}, null),
                    new OpcodeDef(this::value_zpY,  4, new int[]{0xb7}, null),
                    new OpcodeDef(this::value_abs,  4, new int[]{0xaf}, null),
                    new OpcodeDef(this::value_absY, 4, new int[]{0xbf}, null),
                    new OpcodeDef(this::value_xInd, 6, new int[]{0xa3}, null),
                    new OpcodeDef(this::value_indY, 5, new int[]{0xb3}, null),
            }),
            new OpcodeType(this::LXA, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm, 2, new int[]{0xab}, null),
            }),
            new OpcodeType(this::RLA, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   5, new int[]{0x27}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0x37}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0x2f}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0x3f}, null),
                    new OpcodeDef(this::address_absY, 7, new int[]{0x3b}, null),
                    new OpcodeDef(this::address_xInd, 8, new int[]{0x23}, null),
                    new OpcodeDef(this::address_indY, 8, new int[]{0x33}, null),
            }),
            new OpcodeType(this::RRA, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   5, new int[]{0x67}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0x77}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0x6f}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0x7f}, null),
                    new OpcodeDef(this::address_absY, 7, new int[]{0x7b}, null),
                    new OpcodeDef(this::address_xInd, 8, new int[]{0x63}, null),
                    new OpcodeDef(this::address_indY, 8, new int[]{0x73}, null),
            }),
            new OpcodeType(this::SAX, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   3, new int[]{0x87}, null),
                    new OpcodeDef(this::address_zpY,  4, new int[]{0x97}, null),
                    new OpcodeDef(this::address_abs,  4, new int[]{0x8f}, null),
                    new OpcodeDef(this::address_xInd, 6, new int[]{0x83}, null),
            }),
            new OpcodeType(this::SBX, new OpcodeDef[]{
                    new OpcodeDef(this::value_imm, 2, new int[]{0xcb}, null),
            }),
            new OpcodeType(this::SHA, new OpcodeDef[]{
                    new OpcodeDef(this::address_absY, 5, new int[]{0x9f}, null),
                    new OpcodeDef(this::address_indY, 6, new int[]{0x93}, null),
            }),
            new OpcodeType(this::SHX, new OpcodeDef[]{
                    new OpcodeDef(this::address_absY, 5, new int[]{0x9e}, null),
            }),
            new OpcodeType(this::SHY, new OpcodeDef[]{
                    new OpcodeDef(this::address_absX, 5, new int[]{0x9c}, null),
            }),
            new OpcodeType(this::SLO, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   5, new int[]{0x07}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0x17}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0x0f}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0x1f}, null),
                    new OpcodeDef(this::address_absY, 7, new int[]{0x1b}, null),
                    new OpcodeDef(this::address_xInd, 8, new int[]{0x03}, null),
                    new OpcodeDef(this::address_indY, 8, new int[]{0x13}, null),
            }),
            new OpcodeType(this::SRE, new OpcodeDef[]{
                    new OpcodeDef(this::address_zp,   5, new int[]{0x47}, null),
                    new OpcodeDef(this::address_zpX,  6, new int[]{0x57}, null),
                    new OpcodeDef(this::address_abs,  6, new int[]{0x4f}, null),
                    new OpcodeDef(this::address_absX, 7, new int[]{0x5f}, null),
                    new OpcodeDef(this::address_absY, 7, new int[]{0x5b}, null),
                    new OpcodeDef(this::address_xInd, 8, new int[]{0x43}, null),
                    new OpcodeDef(this::address_indY, 8, new int[]{0x53}, null),
            }),
            new OpcodeType(this::TAS, new OpcodeDef[]{
                    new OpcodeDef(this::address_absY, 5, new int[]{0x9b}, null),
            }),
            new OpcodeType(this::JAM, new OpcodeDef[]{
                    new OpcodeDef(null, 0, new int[]{0x02, 0x12, 0x22, 0x32, 0x42, 0x52,
                                                                  0x62, 0x72, 0x92, 0xb2, 0xd2, 0xf2}, null),
            }),
    };
    private final Opcode[] opcodes = new Opcode[0x100];

    private static final int[] INTERRUPT_ADDRESSES = {
            0xfffc,  // Reset
            0xfffa,  // NMI
            0xfffe,  // IRQ/BRK
    };
    private static final int MAGIC = 0xee;

    private int A  = 0x00;
    private int X  = 0x00;
    private int Y  = 0x00;
    private int S  = 0x00;
    private int P  = 0x00;
    private int PC = 0x0000;

    private int     cycles = 0;
    private boolean jam    = false;

    private boolean stepped = false;

    private static final String[] STATUS_NAMES = {"A", "X", "Y", "S", "P", "PC", "MDR"};
    private final VBox statusText;

    public CPU() {
        super("CPU Status");

        statusText = new VBox(Cat65.SPACING);
        for (String statusName : STATUS_NAMES) {
            Label lbl_reg = new Label();
            lbl_reg.setId(String.format("reg-%s", statusName));
            lbl_reg.getStyleClass().add("CPU-reg");
            statusText.getChildren().add(lbl_reg);
        }
        this.getChildren().add(statusText);

        makeOpcodes();
    }

    private void makeOpcodes() {
        for (OpcodeType opT : opcodeTypes) {
            for (OpcodeDef opD : opT.opcodeDefinitions()) {
                Opcode opcode = new Opcode(opT.method(), opD.input(), opD.args(), opD.cycles());
                for (int b : opD.opcodes()) {
                    if (opcodes[b] != null) throw new OpcodeException(b, opcodes[b], opcode);
                    opcodes[b] = opcode;
                }
            }
        }
        for (int o = 0; o < opcodes.length; o++) {
            if (opcodes[o] == null) throw new OpcodeException(o);
        }
    }

    public void clock() {
        if (cycles > 0) {
            cycles--;
        } else if (cycles == 0) {
            if (stepped) {
                stepped = false;
                CMU.step = false;
            }
            if (CMU.step) {
                stepped = true;
            }

            if (jam) {
                CMU.read(0xffff);
            } else {
                if (CMU.pollNMI()) {
                    // NMIs have priority
                    hardwareInterrupt(1);
                } else if (!CMU.pollIRQ() & (!getFlag('I'))) {
                    //    ^^^ IRQs are active-low
                    hardwareInterrupt(2);
                } else {
                    int opcode = nextByte();
                    Opcode op  = opcodes[opcode];
                    cycles     = op.cycles;
                    op.execute();
                }
            }
        }
    }
    private void hardwareInterrupt(int i) {
        cycles = 7;
        stackPushWord(PC);
        stackPush(P | 0b0010_0000);  // bit 5 gets pushed as 1 by IRQs and NMIs
        setFlag('I');
        PC = vectorAddress(i);
    }
    public void reset() {
        jam = false;
        cycles = 7;

        // left over BRK read (would read the opcode)
        nextByte();
        // dummy read left over from BRK
        nextByte();
        // suppressed stack writes (converted to reads)
        CMU.read(0x0100 | S);
        S = (S - 1) & 0xff;
        CMU.read(0x0100 | S);
        S = (S - 1) & 0xff;
        CMU.read(0x0100 | S);
        S = (S - 1) & 0xff;
        // Status Flags get reset too
        P = (P & 0b1100_0011) | 0b0000_0100;
        // read $fffc and $fffd to get the PC
        PC = vectorAddress(0);
    }

    private int getFlagMask(char flag) {
        return switch (flag) {
            case 'N' -> 0x80;
            case 'V' -> 0x40;
            case 'D' -> 0x08;
            case 'I' -> 0x04;
            case 'Z' -> 0x02;
            case 'C' -> 0x01;
            default  -> 0x00;
        };
    }
    private boolean getFlag(char flag) {
        int mask = getFlagMask(flag);
        return (P & mask) != 0;
    }
    private void clearFlag(char flag) {
        int mask = getFlagMask(flag);
        P &= (mask ^ 0xff);
    }
    private void setFlag(char flag) {
        int mask = getFlagMask(flag);
        P |= mask;
    }
    private void writeFlag(char flag, boolean v) {
        if (v) {
            setFlag(flag);
        } else {
            clearFlag(flag);
        }
    }
    private void flagsZN(int v) {
        writeFlag('Z', v == 0x00);
        writeFlag('N', (v & 0x80) != 0);
    }

    private void stackPush(int v) {
        CMU.write(0x0100 | S, v);
        S = (S - 1) & 0xff;
    }
    private void stackPushWord(int v) {
        stackPush(v >> 8);
        stackPush(v & 0x00ff);
    }
    private int stackPull() {
        S = (S + 1) & 0xff;
        return CMU.read(0x0100 | S);
    }
    private int stackPullWord() {
        return stackPull() | (stackPull() << 8);
    }

    private int nextByte() {
        int v = CMU.read(PC);
        PC++;
        PC &= 0xffff;
        return v;
    }
    private int nextWord() {
        int low = nextByte();
        int high = nextByte();
        return ((high << 8) | low);
    }
    private int vectorAddress(int i) {
        int low  = CMU.read(INTERRUPT_ADDRESSES[i]);
        int high = CMU.read(INTERRUPT_ADDRESSES[i] + 1);
        return ((high << 8) | low);
    }
    private int indexedAddress(int address, int i) {
        int a = address + i;
        if ((address & 0xff00) != (a & 0xff00)) {
            cycles++;
        }
        return a & 0xffff;
    }

    private int address_zp() {
        return nextByte();
    }
    private int address_zpXY(int i) {
        return (nextByte() + i) & 0xff;
    }
    private int address_zpX() {
        return address_zpXY(X);
    }
    private int address_zpY() {
        return address_zpXY(Y);
    }
    private int address_abs() {
        return nextWord();
    }
    private int address_absXY(int i) {
        return indexedAddress(nextWord(), i);
    }
    private int address_absX() {
        return indexedAddress(nextWord(), X);
    }
    private int address_absY() {
        return indexedAddress(nextWord(), Y);
    }
    private int address_ind() {
        int i = nextWord();
        int j = ((i & 0xff) == 0xff) ? (i & 0xff00) : i + 1;
        return (CMU.read(i) | (CMU.read(j) << 8));
    }
    private int address_xInd() {
        int a = (nextByte() + X) & 0xff;
        return (CMU.read(a) | (CMU.read(a + 1) << 8));
    }
    private int address_indY() {
        int a = nextByte();
        return indexedAddress(CMU.read(a) | (CMU.read((a + 1) & 0xff) << 8), Y);
    }

    private int value_imm() {
        return nextByte();
    }
    private int value_zp() {
        return CMU.read(address_zp());
    }
    private int value_zpX() {
        return CMU.read(address_zpXY(X));
    }
    private int value_zpY() {
        return CMU.read(address_zpXY(Y));
    }
    private int value_abs() {
        return CMU.read(address_abs());
    }
    private int value_absX() {
        return CMU.read(address_absXY(X));
    }
    private int value_absY() {
        return CMU.read(address_absXY(Y));
    }
    private int value_ind() {
        return CMU.read(address_ind());
    }
    private int value_xInd() {
        return CMU.read(address_xInd());
    }
    private int value_indY() {
        return CMU.read(address_indY());
    }

    private void ADC(OpcodeContext c) {
        int carry = (getFlag('C') ? 1 : 0);
        int v1 = A;
        int r;
        if (getFlag('D')) {  // decimal mode
            int d1 = EmuHelper.fromBCD(v1);
            int d2 = EmuHelper.fromBCD(c.input);
            r = d1 + d2 + carry;
            A = EmuHelper.toBCD(r % 100);
            writeFlag('C', r > 99);
        } else {
            r = v1 + c.input + carry;
            A = r & 0xff;
            writeFlag('C', r > 0xff);
        }
        flagsZN(A);
        writeFlag('V', ((~(v1 ^ c.input)) & (v1 ^ r) & 0x80) != 0);
    }
    private void AND(OpcodeContext c) {
        A &= c.input;
        flagsZN(A);
    }
    private void ASL(OpcodeContext c) {
        int v;
        if (c.hasInput) {
            v = CMU.read(c.input) << 1;
            CMU.write(c.input, v);
        } else {
            v = A << 1;
            A = v & 0xff;
        }
        writeFlag('C', v > 0xff);
        flagsZN(v & 0xff);
    }
    private void B__(OpcodeContext c) {
        int d = value_imm();
        if (getFlag(c.args.charAt(0)) == (c.args.charAt(1) == 'S')) {
            cycles++;
            int o = PC;
            PC += EmuHelper.fromTwosComp(d);
            PC &= 0xffff;
            if (!((PC & 0xff00) == (o & 0xff00))) {
                cycles++;
            }
        }
    }
    private void BIT(OpcodeContext c) {
        writeFlag('Z', (c.input & A)    == 0);
        writeFlag('N', (c.input & 0x80) != 0);
        writeFlag('V', (c.input & 0x40) != 0);
    }
    private void BRK(OpcodeContext c) {
        nextByte();
        stackPushWord(PC);
        stackPush(P | 0b0011_0000);  // bit 5 and the B flag both get pushed as 1 by BRK
        setFlag('I');
        PC = vectorAddress(2);
    }
    private void C__(int reg, int v) {
        int r = (reg - v) & 0xff;
        flagsZN(r);
        writeFlag('C', v <= reg);
    }
    private void CMP(OpcodeContext c) {
        C__(A, c.input);
    }
    private void CPX(OpcodeContext c) {
        C__(X, c.input);
    }
    private void CPY(OpcodeContext c) {
        C__(Y, c.input);
    }
    private void DEC(OpcodeContext c) {
        int v = (CMU.read(c.input) - 1) & 0xff;
        CMU.write(c.input, v);
        flagsZN(v);
    }
    private void DEX(OpcodeContext c) {
        X = (X - 1) & 0xff;
        flagsZN(X);
    }
    private void DEY(OpcodeContext c) {
        Y = (Y - 1) & 0xff;
        flagsZN(Y);
    }
    private void EOR(OpcodeContext c) {
        A ^= c.input;
        flagsZN(A);
    }
    private void SE_(OpcodeContext c) {
        setFlag(c.args.charAt(0));
    }
    private void CL_(OpcodeContext c) {
        clearFlag(c.args.charAt(0));
    }
    private void INC(OpcodeContext c) {
        int v = (CMU.read(c.input) + 1) & 0xff;
        CMU.write(c.input, v);
        flagsZN(v);
    }
    private void INX(OpcodeContext c) {
        X = (X + 1) & 0xff;
        flagsZN(X);
    }
    private void INY(OpcodeContext c) {
        Y = (Y + 1) & 0xff;
        flagsZN(Y);
    }
    private void JMP(OpcodeContext c) {
        PC = c.input;
    }
    private void JSR(OpcodeContext c) {
        stackPushWord(PC - 1);
        PC = c.input;
    }
    private void LDA(OpcodeContext c) {
        A = c.input;
        flagsZN(A);
    }
    private void LDX(OpcodeContext c) {
        X = c.input;
        flagsZN(X);
    }
    private void LDY(OpcodeContext c) {
        Y = c.input;
        flagsZN(Y);
    }
    private void LSR(OpcodeContext c) {
        int v;
        if (c.hasInput) {
            v = CMU.read(c.input);
            writeFlag('C', (v & 0x01) != 0);
            v >>= 1;
            CMU.write(c.input, v);
        } else {
            writeFlag('C', (A & 0x01) != 0);
            A >>= 1;
            v = A;
        }
        flagsZN(v);
    }
    private void NOP(OpcodeContext c) {
        // wow :3c
        // such empty >w<
    }
    private void ORA(OpcodeContext c) {
        A |= c.input;
        flagsZN(A);
    }
    private void PH_(OpcodeContext c) {
        char r = c.args.charAt(0);
        if (r == 'A') {
            stackPush(A);
        } else {
            stackPush(P | 0b0011_0000);  // bit 5 and the B flag both get pushed as 1 by PHP
        }
    }
    private void PL_(OpcodeContext c) {
        char r = c.args.charAt(0);
        if (r == 'A') {
            A = stackPull();
            flagsZN(A);
        } else {
            P = (stackPull() & 0b1100_1111);  // bit 5 and the B flag get ignored by PLP
        }
    }
    private void T__(OpcodeContext c) {
        // register Transfers
        char s = c.args.charAt(0);
        char d = c.args.charAt(1);
        int v = switch (s) {
            case 'A' -> A;
            case 'X' -> X;
            case 'Y' -> Y;
            case 'S' -> S;
            default  -> 0x00;
        };
        switch (d) {
            case 'A' -> A = v;
            case 'X' -> X = v;
            case 'Y' -> Y = v;
            case 'S' -> S = v;
        }
        if (d != 'S') {
            flagsZN(v);
        }
    }
    private void ROL(OpcodeContext c) {
        int carry = (getFlag('C') ? 0x01 : 0x00);
        int o;
        int v;
        if (c.hasInput) {
            o = CMU.read(c.input);
            v = ((o << 1) | carry) & 0xff;
            CMU.write(c.input, v);
        } else {
            o = A;
            A = v = ((o << 1) | carry) & 0xff;
        }
        writeFlag('C', (o & 0x80) != 0);
        flagsZN(v);
    }
    private void ROR(OpcodeContext c) {
        int carry = (getFlag('C') ? 0x80 : 0x00);
        int o;
        int v;
        if (c.hasInput) {
            o = CMU.read(c.input);
            v = ((o >> 1) | carry) & 0xff;
            CMU.write(c.input, v);
        } else {
            o = A;
            A = v = ((o >> 1) | carry) & 0xff;
        }
        writeFlag('C', (o & 0x01) != 0);
        flagsZN(v);
    }
    private void RTI(OpcodeContext c) {
        P  = (stackPull() & 0b1100_1111);  // bit 5 and the B flag get ignored by RTI
        PC = stackPullWord();
    }
    private void RTS(OpcodeContext c) {
        PC = (stackPullWord() + 1) & 0xffff;
    }
    private void SBC(OpcodeContext c) {
        int carry = (getFlag('C') ? 0 : 1);
        int v1 = A;
        int r;
        if (getFlag('D')) {  // decimal mode
            int d1 = EmuHelper.fromBCD(v1);
            int d2 = EmuHelper.fromBCD(c.input);
            r = d1 - d2 - carry;
            A = EmuHelper.toBCD((100 + r) % 100);
        } else {
            r = v1 - c.input - carry;
            A = r & 0xff;
        }
        writeFlag('C', r >= 0x00);
        flagsZN(A);
        writeFlag('V', ((v1 ^ c.input) & (v1 ^ r) & 0x80) != 0);
    }
    private void STA(OpcodeContext c) {
        CMU.write(c.input, A);
    }
    private void STX(OpcodeContext c) {
        CMU.write(c.input, X);
    }
    private void STY(OpcodeContext c) {
        CMU.write(c.input, Y);
    }

    private void ALR(OpcodeContext c) {  // (ASR)
        AND(c);
        // to effectively LSR A
        c.hasInput = false;
        LSR(c);
    }
    private void ANC(OpcodeContext c) {  // (AAC)
        AND(c);
        writeFlag('C', getFlag('N'));
    }
    private void ANE(OpcodeContext c) {  // (XAA)
        A = (A | MAGIC) & X & c.input;
        flagsZN(A);
    }
    private void ARR(OpcodeContext c) {
        AND(c);
        // to effectively ROR A
        c.hasInput = false;
        ROR(c);
        boolean b = (A & 0x40) != 0;
        writeFlag('C', b);
        writeFlag('V', b ^ ((A & 0x20) != 0));
    }
    private void DCP(OpcodeContext c) {  // (DCM)
        DEC(c);
        c.input = CMU.read(c.input);
        CMP(c);
    }
    private void ISC(OpcodeContext c) {  // (ISB, INS)
        DEC(c);
        c.input = CMU.read(c.input);
        SBC(c);
    }
    private void LAS(OpcodeContext c) {  // (LAR, LAE)
        A = X = S = S & c.input;
        flagsZN(A);
    }
    private void LAX(OpcodeContext c) {
        A = X = c.input;
        flagsZN(A);
    }
    private void LXA(OpcodeContext c) {  // (ATX, OAL)
        AND(c);
        c.args = "AX";
        T__(c);
    }
    private void RLA(OpcodeContext c) {
        ROL(c);
        c.input = CMU.read(c.input);
        AND(c);
    }
    private void RRA(OpcodeContext c) {
        ROR(c);
        c.input = CMU.read(c.input);
        AND(c);
    }
    private void SAX(OpcodeContext c) {  // (AXS, AAX)
        int r = A & X;
        CMU.write(c.input, r);
    }
    private void SBX(OpcodeContext c) {  // (AXS, SAX)
        int o = A & X;
        X = (o - c.input) & 0xff;
        writeFlag('C', c.input <= o);
        flagsZN(X);
    }
    private void SHA(OpcodeContext c) {  // (AXA)
        int o = (c.input - Y) & 0xffff;
        int low = o & 0xff;
        int high = o >> 8;
        int a;
        if ((low + Y) > 0xff) {  // crossed page
            a = ((high & X) << 8) | low + Y;
        } else {
            a = (high << 8) | low + Y;
        }
        int v = A & X & (high + 1);
        CMU.write(a, v);
    }
    private void SHX(OpcodeContext c) {  // (SXA, XAS)
        int o = (c.input - Y) & 0xffff;
        int low = o & 0xff;
        int high = o >> 8;
        int a;
        if ((low + Y) > 0xff) {  // crossed page
            a = ((high & X) << 8) | low + Y;
        } else {
            a = (high << 8) | low + Y;
        }
        int v = X & (high + 1);
        CMU.write(a, v);
        // see SHA
    }
    private void SHY(OpcodeContext c) {  // (SYA, SAY)
        int o = (c.input - X) & 0xffff;
        int low = o & 0xff;
        int high = o >> 8;
        int a;
        if ((low + X) > 0xff) {  // crossed page
            a = ((high & Y) << 8) | low + X;
        } else {
            a = (high << 8) | low + X;
        }
        int v = Y & (high + 1);
        CMU.write(a, v);
        // see SHA
    }
    private void SLO(OpcodeContext c) {  // (ASO)
        ASL(c);
        c.input = CMU.read(c.input);
        ORA(c);
    }
    private void SRE(OpcodeContext c) {  // (LSE)
        LSR(c);
        c.input = CMU.read(c.input);
        EOR(c);
    }
    private void TAS(OpcodeContext c) {  // (XAS, SHS)
        S = A & X;  // first set the stack pointer
        // then write to memory using the new value of the stack pointer
        int o = (c.input - Y) & 0xffff;
        int low = o & 0xff;
        int high = o >> 8;
        int a;
        if ((low + Y) > 0xff) {  // crossed page
            a = ((high & S) << 8) | low + Y;
        } else {
            a = (high << 8) | low + Y;
        }
        int v = S & (high + 1);
        CMU.write(a, v);
    }
    private void JAM(OpcodeContext c) {  // (KIL, HLT)
        jam = true;
    }

    @Override
    public void updateWindow() {
        for (Node node : statusText.getChildren()) {
            if (node instanceof Label label) {
                String regName = label.getId().substring(4);
                int value = switch (regName) {
                    case "A"   -> A;
                    case "X"   -> X;
                    case "Y"   -> Y;
                    case "S"   -> S;
                    case "P"   -> P;
                    case "PC"  -> PC;
                    case "MDR" -> CMU.getMDR();
                    default        -> 0x00;
                };
                boolean is16bit = regName.equals("PC");
                label.setText(String.format(String.format("%%3s:  %%5d   |  %%s%%0%dx  |  %%s%%s  |  \"%%s\"", is16bit ? 4 : 2),
                        regName, value, is16bit? "" : "  ", value, is16bit ? "" : "        ",
                        EmuHelper.getBinary(value, is16bit), EmuHelper.getBinaryString(value, is16bit)));
            }
        }
    }
}

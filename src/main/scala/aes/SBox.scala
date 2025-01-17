package aes

import chisel3._
import chisel3.util._

object SBox {
    def apply(byte: UInt): UInt = {
      val sBox = Wire(Vec(256, UInt(8.W)))

      sBox(0)  := "h63".U; sBox(1)    := "h7C".U; sBox(2)   := "h77".U; sBox(3)   := "h7B".U; sBox(4)   := "hF2".U; sBox(5)   := "h6B".U; sBox(6)   := "h6F".U; sBox(7)   := "hC5".U
      sBox(8)  := "h30".U; sBox(9)    := "h01".U; sBox(10)  := "h67".U; sBox(11)  := "h2B".U; sBox(12)  := "hFE".U; sBox(13)  := "hD7".U; sBox(14)  := "hAB".U; sBox(15)  := "h76".U
      sBox(16) := "hCA".U; sBox(17)   := "h82".U; sBox(18)  := "hC9".U; sBox(19)  := "h7D".U; sBox(20)  := "hFA".U; sBox(21)  := "h59".U; sBox(22)  := "h47".U; sBox(23)  := "hF0".U
      sBox(24) := "hAD".U; sBox(25)   := "hD4".U; sBox(26)  := "hA2".U; sBox(27)  := "hAF".U; sBox(28)  := "h9C".U; sBox(29)  := "hA4".U; sBox(30)  := "h72".U; sBox(31)  := "hC0".U
      sBox(32) := "hB7".U; sBox(33)   := "hFD".U; sBox(34)  := "h93".U; sBox(35)  := "h26".U; sBox(36)  := "h36".U; sBox(37)  := "h3F".U; sBox(38)  := "hF7".U; sBox(39)  := "hCC".U
      sBox(40) := "h34".U; sBox(41)   := "hA5".U; sBox(42)  := "hE5".U; sBox(43)  := "hF1".U; sBox(44)  := "h71".U; sBox(45)  := "hD8".U; sBox(46)  := "h31".U; sBox(47)  := "h15".U
      sBox(48) := "h04".U; sBox(49)   := "hC7".U; sBox(50)  := "h23".U; sBox(51)  := "hC3".U; sBox(52)  := "h18".U; sBox(53)  := "h96".U; sBox(54)  := "h05".U; sBox(55)  := "h9A".U
      sBox(56) := "h07".U; sBox(57)   := "h12".U; sBox(58)  := "h80".U; sBox(59)  := "hE2".U; sBox(60)  := "hEB".U; sBox(61)  := "h27".U; sBox(62)  := "hB2".U; sBox(63)  := "h75".U
      sBox(64) := "h09".U; sBox(65)   := "h83".U; sBox(66)  := "h2C".U; sBox(67)  := "h1A".U; sBox(68)  := "h1B".U; sBox(69)  := "h6E".U; sBox(70)  := "h5A".U; sBox(71)  := "hA0".U
      sBox(72) := "h52".U; sBox(73)   := "h3B".U; sBox(74)  := "hD6".U; sBox(75)  := "hB3".U; sBox(76)  := "h29".U; sBox(77)  := "hE3".U; sBox(78)  := "h2F".U; sBox(79)  := "h84".U
      sBox(80) := "h53".U; sBox(81)   := "hD1".U; sBox(82)  := "h00".U; sBox(83)  := "hED".U; sBox(84)  := "h20".U; sBox(85)  := "hFC".U; sBox(86)  := "hB1".U; sBox(87)  := "h5B".U
      sBox(88) := "h6A".U; sBox(89)   := "hCB".U; sBox(90)  := "hBE".U; sBox(91)  := "h39".U; sBox(92)  := "h4A".U; sBox(93)  := "h4C".U; sBox(94)  := "h58".U; sBox(95)  := "hCF".U
      sBox(96) := "hD0".U; sBox(97)   := "hEF".U; sBox(98)  := "hAA".U; sBox(99)  := "hFB".U; sBox(100) := "h43".U; sBox(101) := "h4D".U; sBox(102) := "h33".U; sBox(103) := "h85".U
      sBox(104) := "h45".U; sBox(105) := "hF9".U; sBox(106) := "h02".U; sBox(107) := "h7F".U; sBox(108) := "h50".U; sBox(109) := "h3C".U; sBox(110) := "h9F".U; sBox(111) := "hA8".U
      sBox(112) := "h51".U; sBox(113) := "hA3".U; sBox(114) := "h40".U; sBox(115) := "h8F".U; sBox(116) := "h92".U; sBox(117) := "h9D".U; sBox(118) := "h38".U; sBox(119) := "hF5".U
      sBox(120) := "hBC".U; sBox(121) := "hB6".U; sBox(122) := "hDA".U; sBox(123) := "h21".U; sBox(124) := "h10".U; sBox(125) := "hFF".U; sBox(126) := "hF3".U; sBox(127) := "hD2".U
      sBox(128) := "hCD".U; sBox(129) := "h0C".U; sBox(130) := "h13".U; sBox(131) := "hEC".U; sBox(132) := "h5F".U; sBox(133) := "h97".U; sBox(134) := "h44".U; sBox(135) := "h17".U
      sBox(136) := "hC4".U; sBox(137) := "hA7".U; sBox(138) := "h7E".U; sBox(139) := "h3D".U; sBox(140) := "h64".U; sBox(141) := "h5D".U; sBox(142) := "h19".U; sBox(143) := "h73".U
      sBox(144) := "h60".U; sBox(145) := "h81".U; sBox(146) := "h4F".U; sBox(147) := "hDC".U; sBox(148) := "h22".U; sBox(149) := "h2A".U; sBox(150) := "h90".U; sBox(151) := "h88".U
      sBox(152) := "h46".U; sBox(153) := "hEE".U; sBox(154) := "hB8".U; sBox(155) := "h14".U; sBox(156) := "hDE".U; sBox(157) := "h5E".U; sBox(158) := "h0B".U; sBox(159) := "hDB".U
      sBox(160) := "hE0".U; sBox(161) := "h32".U; sBox(162) := "h3A".U; sBox(163) := "h0A".U; sBox(164) := "h49".U; sBox(165) := "h06".U; sBox(166) := "h24".U; sBox(167) := "h5C".U
      sBox(168) := "hC2".U; sBox(169) := "hD3".U; sBox(170) := "hAC".U; sBox(171) := "h62".U; sBox(172) := "h91".U; sBox(173) := "h95".U; sBox(174) := "hE4".U; sBox(175) := "h79".U
      sBox(176) := "hE7".U; sBox(177) := "hC8".U; sBox(178) := "h37".U; sBox(179) := "h6D".U; sBox(180) := "h8D".U; sBox(181) := "hD5".U; sBox(182) := "h4E".U; sBox(183) := "hA9".U
      sBox(184) := "h6C".U; sBox(185) := "h56".U; sBox(186) := "hF4".U; sBox(187) := "hEA".U; sBox(188) := "h65".U; sBox(189) := "h7A".U; sBox(190) := "hAE".U; sBox(191) := "h08".U
      sBox(192) := "hBA".U; sBox(193) := "h78".U; sBox(194) := "h25".U; sBox(195) := "h2E".U; sBox(196) := "h1C".U; sBox(197) := "hA6".U; sBox(198) := "hB4".U; sBox(199) := "hC6".U
      sBox(200) := "hE8".U; sBox(201) := "hDD".U; sBox(202) := "h74".U; sBox(203) := "h1F".U; sBox(204) := "h4B".U; sBox(205) := "hBD".U; sBox(206) := "h8B".U; sBox(207) := "h8A".U
      sBox(208) := "h70".U; sBox(209) := "h3E".U; sBox(210) := "hB5".U; sBox(211) := "h66".U; sBox(212) := "h48".U; sBox(213) := "h03".U; sBox(214) := "hF6".U; sBox(215) := "h0E".U
      sBox(216) := "h61".U; sBox(217) := "h35".U; sBox(218) := "h57".U; sBox(219) := "hB9".U; sBox(220) := "h86".U; sBox(221) := "hC1".U; sBox(222) := "h1D".U; sBox(223) := "h9E".U
      sBox(224) := "hE1".U; sBox(225) := "hF8".U; sBox(226) := "h98".U; sBox(227) := "h11".U; sBox(228) := "h69".U; sBox(229) := "hD9".U; sBox(230) := "h8E".U; sBox(231) := "h94".U
      sBox(232) := "h9B".U; sBox(233) := "h1E".U; sBox(234) := "h87".U; sBox(235) := "hE9".U; sBox(236) := "hCE".U; sBox(237) := "h55".U; sBox(238) := "h28".U; sBox(239) := "hDF".U
      sBox(240) := "h8C".U; sBox(241) := "hA1".U; sBox(242) := "h89".U; sBox(243) := "h0D".U; sBox(244) := "hBF".U; sBox(245) := "hE6".U; sBox(246) := "h42".U; sBox(247) := "h68".U
      sBox(248) := "h41".U; sBox(249) := "h99".U; sBox(250) := "h2D".U; sBox(251) := "h0F".U; sBox(252) := "hB0".U; sBox(253) := "h54".U; sBox(254) := "hBB".U; sBox(255) := "h16".U

      sBox(byte)
    }
}


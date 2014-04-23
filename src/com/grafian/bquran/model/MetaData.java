package com.grafian.bquran.model;

public class MetaData {

	public static class Sura {
		final public static int MECCAN = 0;
		final public static int MEDINAN = 1;
		public int index;
		public int start;
		public int ayas;
		public int order;
		public int rukus;
		public String name;
		public String tname;
		public String ename;
		public int type;

		public Sura(int index, int start, int ayas, int order, int rukus, String name, String tname, String ename, int type) {
			this.index = index;
			this.start = start;
			this.ayas = ayas;
			this.order = order;
			this.rukus = rukus;
			this.name = name;
			this.tname = tname;
			this.ename = ename;
			this.type = type;
		}
	}

	public static class Mark {
		public int sura;
		public int aya;

		public Mark() {
		}

		public Mark(int sura, int aya) {
			this.sura = sura;
			this.aya = aya;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Mark) {
				Mark m = (Mark) o;
				return m.sura == sura && m.aya == aya;
			}
			return false;
		}
	}

	final private Sura[] mSuras = {
			new Sura(1, 0, 7, 5, 1, "الفاتحة", "Al-Faatiha", "The Opening", Sura.MECCAN),
			new Sura(2, 7, 286, 87, 40, "البقرة", "Al-Baqara", "The Cow", Sura.MEDINAN),
			new Sura(3, 293, 200, 89, 20, "آل عمران", "Aal-i-Imraan", "The Family of Imraan", Sura.MEDINAN),
			new Sura(4, 493, 176, 92, 24, "النساء", "An-Nisaa", "The Women", Sura.MEDINAN),
			new Sura(5, 669, 120, 112, 16, "المائدة", "Al-Maaida", "The Table", Sura.MEDINAN),
			new Sura(6, 789, 165, 55, 20, "الأنعام", "Al-An'aam", "The Cattle", Sura.MECCAN),
			new Sura(7, 954, 206, 39, 24, "الأعراف", "Al-A'raaf", "The Heights", Sura.MECCAN),
			new Sura(8, 1160, 75, 88, 10, "الأنفال", "Al-Anfaal", "The Spoils of War", Sura.MEDINAN),
			new Sura(9, 1235, 129, 113, 16, "التوبة", "At-Tawba", "The Repentance", Sura.MEDINAN),
			new Sura(10, 1364, 109, 51, 11, "يونس", "Yunus", "Jonas", Sura.MECCAN),
			new Sura(11, 1473, 123, 52, 10, "هود", "Hud", "Hud", Sura.MECCAN),
			new Sura(12, 1596, 111, 53, 12, "يوسف", "Yusuf", "Joseph", Sura.MECCAN),
			new Sura(13, 1707, 43, 96, 6, "الرعد", "Ar-Ra'd", "The Thunder", Sura.MEDINAN),
			new Sura(14, 1750, 52, 72, 7, "ابراهيم", "Ibrahim", "Abraham", Sura.MECCAN),
			new Sura(15, 1802, 99, 54, 6, "الحجر", "Al-Hijr", "The Rock", Sura.MECCAN),
			new Sura(16, 1901, 128, 70, 16, "النحل", "An-Nahl", "The Bee", Sura.MECCAN),
			new Sura(17, 2029, 111, 50, 12, "الإسراء", "Al-Israa", "The Night Journey", Sura.MECCAN),
			new Sura(18, 2140, 110, 69, 12, "الكهف", "Al-Kahf", "The Cave", Sura.MECCAN),
			new Sura(19, 2250, 98, 44, 6, "مريم", "Maryam", "Mary", Sura.MECCAN),
			new Sura(20, 2348, 135, 45, 8, "طه", "Taa-Haa", "Taa-Haa", Sura.MECCAN),
			new Sura(21, 2483, 112, 73, 7, "الأنبياء", "Al-Anbiyaa", "The Prophets", Sura.MECCAN),
			new Sura(22, 2595, 78, 103, 10, "الحج", "Al-Hajj", "The Pilgrimage", Sura.MEDINAN),
			new Sura(23, 2673, 118, 74, 6, "المؤمنون", "Al-Muminoon", "The Believers", Sura.MECCAN),
			new Sura(24, 2791, 64, 102, 9, "النور", "An-Noor", "The Light", Sura.MEDINAN),
			new Sura(25, 2855, 77, 42, 6, "الفرقان", "Al-Furqaan", "The Criterion", Sura.MECCAN),
			new Sura(26, 2932, 227, 47, 11, "الشعراء", "Ash-Shu'araa", "The Poets", Sura.MECCAN),
			new Sura(27, 3159, 93, 48, 7, "النمل", "An-Naml", "The Ant", Sura.MECCAN),
			new Sura(28, 3252, 88, 49, 8, "القصص", "Al-Qasas", "The Stories", Sura.MECCAN),
			new Sura(29, 3340, 69, 85, 7, "العنكبوت", "Al-Ankaboot", "The Spider", Sura.MECCAN),
			new Sura(30, 3409, 60, 84, 6, "الروم", "Ar-Room", "The Romans", Sura.MECCAN),
			new Sura(31, 3469, 34, 57, 3, "لقمان", "Luqman", "Luqman", Sura.MECCAN),
			new Sura(32, 3503, 30, 75, 3, "السجدة", "As-Sajda", "The Prostration", Sura.MECCAN),
			new Sura(33, 3533, 73, 90, 9, "الأحزاب", "Al-Ahzaab", "The Clans", Sura.MEDINAN),
			new Sura(34, 3606, 54, 58, 6, "سبإ", "Saba", "Sheba", Sura.MECCAN),
			new Sura(35, 3660, 45, 43, 5, "فاطر", "Faatir", "The Originator", Sura.MECCAN),
			new Sura(36, 3705, 83, 41, 5, "يس", "Yaseen", "Yaseen", Sura.MECCAN),
			new Sura(37, 3788, 182, 56, 5, "الصافات", "As-Saaffaat", "Those drawn up in Ranks", Sura.MECCAN),
			new Sura(38, 3970, 88, 38, 5, "ص", "Saad", "The letter Saad", Sura.MECCAN),
			new Sura(39, 4058, 75, 59, 8, "الزمر", "Az-Zumar", "The Groups", Sura.MECCAN),
			new Sura(40, 4133, 85, 60, 9, "غافر", "Al-Ghaafir", "The Forgiver", Sura.MECCAN),
			new Sura(41, 4218, 54, 61, 6, "فصلت", "Fussilat", "Explained in detail", Sura.MECCAN),
			new Sura(42, 4272, 53, 62, 5, "الشورى", "Ash-Shura", "Consultation", Sura.MECCAN),
			new Sura(43, 4325, 89, 63, 7, "الزخرف", "Az-Zukhruf", "Ornaments of gold", Sura.MECCAN),
			new Sura(44, 4414, 59, 64, 3, "الدخان", "Ad-Dukhaan", "The Smoke", Sura.MECCAN),
			new Sura(45, 4473, 37, 65, 4, "الجاثية", "Al-Jaathiya", "Crouching", Sura.MECCAN),
			new Sura(46, 4510, 35, 66, 4, "الأحقاف", "Al-Ahqaf", "The Dunes", Sura.MECCAN),
			new Sura(47, 4545, 38, 95, 4, "محمد", "Muhammad", "Muhammad", Sura.MEDINAN),
			new Sura(48, 4583, 29, 111, 4, "الفتح", "Al-Fath", "The Victory", Sura.MEDINAN),
			new Sura(49, 4612, 18, 106, 2, "الحجرات", "Al-Hujuraat", "The Inner Apartments", Sura.MEDINAN),
			new Sura(50, 4630, 45, 34, 3, "ق", "Qaaf", "The letter Qaaf", Sura.MECCAN),
			new Sura(51, 4675, 60, 67, 3, "الذاريات", "Adh-Dhaariyat", "The Winnowing Winds", Sura.MECCAN),
			new Sura(52, 4735, 49, 76, 2, "الطور", "At-Tur", "The Mount", Sura.MECCAN),
			new Sura(53, 4784, 62, 23, 3, "النجم", "An-Najm", "The Star", Sura.MECCAN),
			new Sura(54, 4846, 55, 37, 3, "القمر", "Al-Qamar", "The Moon", Sura.MECCAN),
			new Sura(55, 4901, 78, 97, 3, "الرحمن", "Ar-Rahmaan", "The Beneficent", Sura.MEDINAN),
			new Sura(56, 4979, 96, 46, 3, "الواقعة", "Al-Waaqia", "The Inevitable", Sura.MECCAN),
			new Sura(57, 5075, 29, 94, 4, "الحديد", "Al-Hadid", "The Iron", Sura.MEDINAN),
			new Sura(58, 5104, 22, 105, 3, "المجادلة", "Al-Mujaadila", "The Pleading Woman", Sura.MEDINAN),
			new Sura(59, 5126, 24, 101, 3, "الحشر", "Al-Hashr", "The Exile", Sura.MEDINAN),
			new Sura(60, 5150, 13, 91, 2, "الممتحنة", "Al-Mumtahana", "She that is to be examined", Sura.MEDINAN),
			new Sura(61, 5163, 14, 109, 2, "الصف", "As-Saff", "The Ranks", Sura.MEDINAN),
			new Sura(62, 5177, 11, 110, 2, "الجمعة", "Al-Jumu'a", "Friday", Sura.MEDINAN),
			new Sura(63, 5188, 11, 104, 2, "المنافقون", "Al-Munaafiqoon", "The Hypocrites", Sura.MEDINAN),
			new Sura(64, 5199, 18, 108, 2, "التغابن", "At-Taghaabun", "Mutual Disillusion", Sura.MEDINAN),
			new Sura(65, 5217, 12, 99, 2, "الطلاق", "At-Talaaq", "Divorce", Sura.MEDINAN),
			new Sura(66, 5229, 12, 107, 2, "التحريم", "At-Tahrim", "The Prohibition", Sura.MEDINAN),
			new Sura(67, 5241, 30, 77, 2, "الملك", "Al-Mulk", "The Sovereignty", Sura.MECCAN),
			new Sura(68, 5271, 52, 2, 2, "القلم", "Al-Qalam", "The Pen", Sura.MECCAN),
			new Sura(69, 5323, 52, 78, 2, "الحاقة", "Al-Haaqqa", "The Reality", Sura.MECCAN),
			new Sura(70, 5375, 44, 79, 2, "المعارج", "Al-Ma'aarij", "The Ascending Stairways", Sura.MECCAN),
			new Sura(71, 5419, 28, 71, 2, "نوح", "Nooh", "Noah", Sura.MECCAN),
			new Sura(72, 5447, 28, 40, 2, "الجن", "Al-Jinn", "The Jinn", Sura.MECCAN),
			new Sura(73, 5475, 20, 3, 2, "المزمل", "Al-Muzzammil", "The Enshrouded One", Sura.MECCAN),
			new Sura(74, 5495, 56, 4, 2, "المدثر", "Al-Muddaththir", "The Cloaked One", Sura.MECCAN),
			new Sura(75, 5551, 40, 31, 2, "القيامة", "Al-Qiyaama", "The Resurrection", Sura.MECCAN),
			new Sura(76, 5591, 31, 98, 2, "الانسان", "Al-Insaan", "Man", Sura.MEDINAN),
			new Sura(77, 5622, 50, 33, 2, "المرسلات", "Al-Mursalaat", "The Emissaries", Sura.MECCAN),
			new Sura(78, 5672, 40, 80, 2, "النبإ", "An-Naba", "The Announcement", Sura.MECCAN),
			new Sura(79, 5712, 46, 81, 2, "النازعات", "An-Naazi'aat", "Those who drag forth", Sura.MECCAN),
			new Sura(80, 5758, 42, 24, 1, "عبس", "Abasa", "He frowned", Sura.MECCAN),
			new Sura(81, 5800, 29, 7, 1, "التكوير", "At-Takwir", "The Overthrowing", Sura.MECCAN),
			new Sura(82, 5829, 19, 82, 1, "الإنفطار", "Al-Infitaar", "The Cleaving", Sura.MECCAN),
			new Sura(83, 5848, 36, 86, 1, "المطففين", "Al-Mutaffifin", "Defrauding", Sura.MECCAN),
			new Sura(84, 5884, 25, 83, 1, "الإنشقاق", "Al-Inshiqaaq", "The Splitting Open", Sura.MECCAN),
			new Sura(85, 5909, 22, 27, 1, "البروج", "Al-Burooj", "The Constellations", Sura.MECCAN),
			new Sura(86, 5931, 17, 36, 1, "الطارق", "At-Taariq", "The Morning Star", Sura.MECCAN),
			new Sura(87, 5948, 19, 8, 1, "الأعلى", "Al-A'laa", "The Most High", Sura.MECCAN),
			new Sura(88, 5967, 26, 68, 1, "الغاشية", "Al-Ghaashiya", "The Overwhelming", Sura.MECCAN),
			new Sura(89, 5993, 30, 10, 1, "الفجر", "Al-Fajr", "The Dawn", Sura.MECCAN),
			new Sura(90, 6023, 20, 35, 1, "البلد", "Al-Balad", "The City", Sura.MECCAN),
			new Sura(91, 6043, 15, 26, 1, "الشمس", "Ash-Shams", "The Sun", Sura.MECCAN),
			new Sura(92, 6058, 21, 9, 1, "الليل", "Al-Lail", "The Night", Sura.MECCAN),
			new Sura(93, 6079, 11, 11, 1, "الضحى", "Ad-Dhuhaa", "The Morning Hours", Sura.MECCAN),
			new Sura(94, 6090, 8, 12, 1, "الشرح", "Ash-Sharh", "The Consolation", Sura.MECCAN),
			new Sura(95, 6098, 8, 28, 1, "التين", "At-Tin", "The Fig", Sura.MECCAN),
			new Sura(96, 6106, 19, 1, 1, "العلق", "Al-Alaq", "The Clot", Sura.MECCAN),
			new Sura(97, 6125, 5, 25, 1, "القدر", "Al-Qadr", "The Power, Fate", Sura.MECCAN),
			new Sura(98, 6130, 8, 100, 1, "البينة", "Al-Bayyina", "The Evidence", Sura.MEDINAN),
			new Sura(99, 6138, 8, 93, 1, "الزلزلة", "Az-Zalzala", "The Earthquake", Sura.MEDINAN),
			new Sura(100, 6146, 11, 14, 1, "العاديات", "Al-Aadiyaat", "The Chargers", Sura.MECCAN),
			new Sura(101, 6157, 11, 30, 1, "القارعة", "Al-Qaari'a", "The Calamity", Sura.MECCAN),
			new Sura(102, 6168, 8, 16, 1, "التكاثر", "At-Takaathur", "Competition", Sura.MECCAN),
			new Sura(103, 6176, 3, 13, 1, "العصر", "Al-Asr", "The Declining Day, Epoch", Sura.MECCAN),
			new Sura(104, 6179, 9, 32, 1, "الهمزة", "Al-Humaza", "The Traducer", Sura.MECCAN),
			new Sura(105, 6188, 5, 19, 1, "الفيل", "Al-Fil", "The Elephant", Sura.MECCAN),
			new Sura(106, 6193, 4, 29, 1, "قريش", "Quraish", "Quraysh", Sura.MECCAN),
			new Sura(107, 6197, 7, 17, 1, "الماعون", "Al-Maa'un", "Almsgiving", Sura.MECCAN),
			new Sura(108, 6204, 3, 15, 1, "الكوثر", "Al-Kawthar", "Abundance", Sura.MECCAN),
			new Sura(109, 6207, 6, 18, 1, "الكافرون", "Al-Kaafiroon", "The Disbelievers", Sura.MECCAN),
			new Sura(110, 6213, 3, 114, 1, "النصر", "An-Nasr", "Divine Support", Sura.MEDINAN),
			new Sura(111, 6216, 5, 6, 1, "المسد", "Al-Masad", "The Palm Fibre", Sura.MECCAN),
			new Sura(112, 6221, 4, 22, 1, "الإخلاص", "Al-Ikhlaas", "Sincerity", Sura.MECCAN),
			new Sura(113, 6225, 5, 20, 1, "الفلق", "Al-Falaq", "The Dawn", Sura.MECCAN),
			new Sura(114, 6230, 6, 21, 1, "الناس", "An-Naas", "Mankind", Sura.MECCAN)
	};

	final private int[][] mJuzs = {
			{ 1, 1 }, { 2, 142 }, { 2, 253 }, { 3, 93 }, { 4, 24 },
			{ 4, 148 }, { 5, 82 }, { 6, 111 }, { 7, 88 }, { 8, 41 },
			{ 9, 93 }, { 11, 6 }, { 12, 53 }, { 15, 1 }, { 17, 1 },
			{ 18, 75 }, { 21, 1 }, { 23, 1 }, { 25, 21 }, { 27, 56 },
			{ 29, 46 }, { 33, 31 }, { 36, 28 }, { 39, 32 }, { 41, 47 },
			{ 46, 1 }, { 51, 31 }, { 58, 1 }, { 67, 1 }, { 78, 1 }
	};

	final private int[][] mHizbs = {
			{ 1, 1 }, { 2, 26 }, { 2, 44 }, { 2, 60 },
			{ 2, 75 }, { 2, 92 }, { 2, 106 }, { 2, 124 },
			{ 2, 142 }, { 2, 158 }, { 2, 177 }, { 2, 189 },
			{ 2, 203 }, { 2, 219 }, { 2, 233 }, { 2, 243 },
			{ 2, 253 }, { 2, 263 }, { 2, 272 }, { 2, 283 },
			{ 3, 15 }, { 3, 33 }, { 3, 52 }, { 3, 75 },
			{ 3, 93 }, { 3, 113 }, { 3, 133 }, { 3, 153 },
			{ 3, 171 }, { 3, 186 }, { 4, 1 }, { 4, 12 },
			{ 4, 24 }, { 4, 36 }, { 4, 58 }, { 4, 74 },
			{ 4, 88 }, { 4, 100 }, { 4, 114 }, { 4, 135 },
			{ 4, 148 }, { 4, 163 }, { 5, 1 }, { 5, 12 },
			{ 5, 27 }, { 5, 41 }, { 5, 51 }, { 5, 67 },
			{ 5, 82 }, { 5, 97 }, { 5, 109 }, { 6, 13 },
			{ 6, 36 }, { 6, 59 }, { 6, 74 }, { 6, 95 },
			{ 6, 111 }, { 6, 127 }, { 6, 141 }, { 6, 151 },
			{ 7, 1 }, { 7, 31 }, { 7, 47 }, { 7, 65 },
			{ 7, 88 }, { 7, 117 }, { 7, 142 }, { 7, 156 },
			{ 7, 171 }, { 7, 189 }, { 8, 1 }, { 8, 22 },
			{ 8, 41 }, { 8, 61 }, { 9, 1 }, { 9, 19 },
			{ 9, 34 }, { 9, 46 }, { 9, 60 }, { 9, 75 },
			{ 9, 93 }, { 9, 111 }, { 9, 122 }, { 10, 11 },
			{ 10, 26 }, { 10, 53 }, { 10, 71 }, { 10, 90 },
			{ 11, 6 }, { 11, 24 }, { 11, 41 }, { 11, 61 },
			{ 11, 84 }, { 11, 108 }, { 12, 7 }, { 12, 30 },
			{ 12, 53 }, { 12, 77 }, { 12, 101 }, { 13, 5 },
			{ 13, 19 }, { 13, 35 }, { 14, 10 }, { 14, 28 },
			{ 15, 1 }, { 15, 50 }, { 16, 1 }, { 16, 30 },
			{ 16, 51 }, { 16, 75 }, { 16, 90 }, { 16, 111 },
			{ 17, 1 }, { 17, 23 }, { 17, 50 }, { 17, 70 },
			{ 17, 99 }, { 18, 17 }, { 18, 32 }, { 18, 51 },
			{ 18, 75 }, { 18, 99 }, { 19, 22 }, { 19, 59 },
			{ 20, 1 }, { 20, 55 }, { 20, 83 }, { 20, 111 },
			{ 21, 1 }, { 21, 29 }, { 21, 51 }, { 21, 83 },
			{ 22, 1 }, { 22, 19 }, { 22, 38 }, { 22, 60 },
			{ 23, 1 }, { 23, 36 }, { 23, 75 }, { 24, 1 },
			{ 24, 21 }, { 24, 35 }, { 24, 53 }, { 25, 1 },
			{ 25, 21 }, { 25, 53 }, { 26, 1 }, { 26, 52 },
			{ 26, 111 }, { 26, 181 }, { 27, 1 }, { 27, 27 },
			{ 27, 56 }, { 27, 82 }, { 28, 12 }, { 28, 29 },
			{ 28, 51 }, { 28, 76 }, { 29, 1 }, { 29, 26 },
			{ 29, 46 }, { 30, 1 }, { 30, 31 }, { 30, 54 },
			{ 31, 22 }, { 32, 11 }, { 33, 1 }, { 33, 18 },
			{ 33, 31 }, { 33, 51 }, { 33, 60 }, { 34, 10 },
			{ 34, 24 }, { 34, 46 }, { 35, 15 }, { 35, 41 },
			{ 36, 28 }, { 36, 60 }, { 37, 22 }, { 37, 83 },
			{ 37, 145 }, { 38, 21 }, { 38, 52 }, { 39, 8 },
			{ 39, 32 }, { 39, 53 }, { 40, 1 }, { 40, 21 },
			{ 40, 41 }, { 40, 66 }, { 41, 9 }, { 41, 25 },
			{ 41, 47 }, { 42, 13 }, { 42, 27 }, { 42, 51 },
			{ 43, 24 }, { 43, 57 }, { 44, 17 }, { 45, 12 },
			{ 46, 1 }, { 46, 21 }, { 47, 10 }, { 47, 33 },
			{ 48, 18 }, { 49, 1 }, { 49, 14 }, { 50, 27 },
			{ 51, 31 }, { 52, 24 }, { 53, 26 }, { 54, 9 },
			{ 55, 1 }, { 56, 1 }, { 56, 75 }, { 57, 16 },
			{ 58, 1 }, { 58, 14 }, { 59, 11 }, { 60, 7 },
			{ 62, 1 }, { 63, 4 }, { 65, 1 }, { 66, 1 },
			{ 67, 1 }, { 68, 1 }, { 69, 1 }, { 70, 19 },
			{ 72, 1 }, { 73, 20 }, { 75, 1 }, { 76, 19 },
			{ 78, 1 }, { 80, 1 }, { 82, 1 }, { 84, 1 },
			{ 87, 1 }, { 90, 1 }, { 94, 1 }, { 100, 9 }
	};

	final private int[][] mPages = {
			{ 1, 1 }, { 2, 1 }, { 2, 6 }, { 2, 17 }, { 2, 25 },
			{ 2, 30 }, { 2, 38 }, { 2, 49 }, { 2, 58 }, { 2, 62 },
			{ 2, 70 }, { 2, 77 }, { 2, 84 }, { 2, 89 }, { 2, 94 },
			{ 2, 102 }, { 2, 106 }, { 2, 113 }, { 2, 120 }, { 2, 127 },
			{ 2, 135 }, { 2, 142 }, { 2, 146 }, { 2, 154 }, { 2, 164 },
			{ 2, 170 }, { 2, 177 }, { 2, 182 }, { 2, 187 }, { 2, 191 },
			{ 2, 197 }, { 2, 203 }, { 2, 211 }, { 2, 216 }, { 2, 220 },
			{ 2, 225 }, { 2, 231 }, { 2, 234 }, { 2, 238 }, { 2, 246 },
			{ 2, 249 }, { 2, 253 }, { 2, 257 }, { 2, 260 }, { 2, 265 },
			{ 2, 270 }, { 2, 275 }, { 2, 282 }, { 2, 283 }, { 3, 1 },
			{ 3, 10 }, { 3, 16 }, { 3, 23 }, { 3, 30 }, { 3, 38 },
			{ 3, 46 }, { 3, 53 }, { 3, 62 }, { 3, 71 }, { 3, 78 },
			{ 3, 84 }, { 3, 92 }, { 3, 101 }, { 3, 109 }, { 3, 116 },
			{ 3, 122 }, { 3, 133 }, { 3, 141 }, { 3, 149 }, { 3, 154 },
			{ 3, 158 }, { 3, 166 }, { 3, 174 }, { 3, 181 }, { 3, 187 },
			{ 3, 195 }, { 4, 1 }, { 4, 7 }, { 4, 12 }, { 4, 15 },
			{ 4, 20 }, { 4, 24 }, { 4, 27 }, { 4, 34 }, { 4, 38 },
			{ 4, 45 }, { 4, 52 }, { 4, 60 }, { 4, 66 }, { 4, 75 },
			{ 4, 80 }, { 4, 87 }, { 4, 92 }, { 4, 95 }, { 4, 102 },
			{ 4, 106 }, { 4, 114 }, { 4, 122 }, { 4, 128 }, { 4, 135 },
			{ 4, 141 }, { 4, 148 }, { 4, 155 }, { 4, 163 }, { 4, 171 },
			{ 4, 176 }, { 5, 3 }, { 5, 6 }, { 5, 10 }, { 5, 14 },
			{ 5, 18 }, { 5, 24 }, { 5, 32 }, { 5, 37 }, { 5, 42 },
			{ 5, 46 }, { 5, 51 }, { 5, 58 }, { 5, 65 }, { 5, 71 },
			{ 5, 77 }, { 5, 83 }, { 5, 90 }, { 5, 96 }, { 5, 104 },
			{ 5, 109 }, { 5, 114 }, { 6, 1 }, { 6, 9 }, { 6, 19 },
			{ 6, 28 }, { 6, 36 }, { 6, 45 }, { 6, 53 }, { 6, 60 },
			{ 6, 69 }, { 6, 74 }, { 6, 82 }, { 6, 91 }, { 6, 95 },
			{ 6, 102 }, { 6, 111 }, { 6, 119 }, { 6, 125 }, { 6, 132 },
			{ 6, 138 }, { 6, 143 }, { 6, 147 }, { 6, 152 }, { 6, 158 },
			{ 7, 1 }, { 7, 12 }, { 7, 23 }, { 7, 31 }, { 7, 38 },
			{ 7, 44 }, { 7, 52 }, { 7, 58 }, { 7, 68 }, { 7, 74 },
			{ 7, 82 }, { 7, 88 }, { 7, 96 }, { 7, 105 }, { 7, 121 },
			{ 7, 131 }, { 7, 138 }, { 7, 144 }, { 7, 150 }, { 7, 156 },
			{ 7, 160 }, { 7, 164 }, { 7, 171 }, { 7, 179 }, { 7, 188 },
			{ 7, 196 }, { 8, 1 }, { 8, 9 }, { 8, 17 }, { 8, 26 },
			{ 8, 34 }, { 8, 41 }, { 8, 46 }, { 8, 53 }, { 8, 62 },
			{ 8, 70 }, { 9, 1 }, { 9, 7 }, { 9, 14 }, { 9, 21 },
			{ 9, 27 }, { 9, 32 }, { 9, 37 }, { 9, 41 }, { 9, 48 },
			{ 9, 55 }, { 9, 62 }, { 9, 69 }, { 9, 73 }, { 9, 80 },
			{ 9, 87 }, { 9, 94 }, { 9, 100 }, { 9, 107 }, { 9, 112 },
			{ 9, 118 }, { 9, 123 }, { 10, 1 }, { 10, 7 }, { 10, 15 },
			{ 10, 21 }, { 10, 26 }, { 10, 34 }, { 10, 43 }, { 10, 54 },
			{ 10, 62 }, { 10, 71 }, { 10, 79 }, { 10, 89 }, { 10, 98 },
			{ 10, 107 }, { 11, 6 }, { 11, 13 }, { 11, 20 }, { 11, 29 },
			{ 11, 38 }, { 11, 46 }, { 11, 54 }, { 11, 63 }, { 11, 72 },
			{ 11, 82 }, { 11, 89 }, { 11, 98 }, { 11, 109 }, { 11, 118 },
			{ 12, 5 }, { 12, 15 }, { 12, 23 }, { 12, 31 }, { 12, 38 },
			{ 12, 44 }, { 12, 53 }, { 12, 64 }, { 12, 70 }, { 12, 79 },
			{ 12, 87 }, { 12, 96 }, { 12, 104 }, { 13, 1 }, { 13, 6 },
			{ 13, 14 }, { 13, 19 }, { 13, 29 }, { 13, 35 }, { 13, 43 },
			{ 14, 6 }, { 14, 11 }, { 14, 19 }, { 14, 25 }, { 14, 34 },
			{ 14, 43 }, { 15, 1 }, { 15, 16 }, { 15, 32 }, { 15, 52 },
			{ 15, 71 }, { 15, 91 }, { 16, 7 }, { 16, 15 }, { 16, 27 },
			{ 16, 35 }, { 16, 43 }, { 16, 55 }, { 16, 65 }, { 16, 73 },
			{ 16, 80 }, { 16, 88 }, { 16, 94 }, { 16, 103 }, { 16, 111 },
			{ 16, 119 }, { 17, 1 }, { 17, 8 }, { 17, 18 }, { 17, 28 },
			{ 17, 39 }, { 17, 50 }, { 17, 59 }, { 17, 67 }, { 17, 76 },
			{ 17, 87 }, { 17, 97 }, { 17, 105 }, { 18, 5 }, { 18, 16 },
			{ 18, 21 }, { 18, 28 }, { 18, 35 }, { 18, 46 }, { 18, 54 },
			{ 18, 62 }, { 18, 75 }, { 18, 84 }, { 18, 98 }, { 19, 1 },
			{ 19, 12 }, { 19, 26 }, { 19, 39 }, { 19, 52 }, { 19, 65 },
			{ 19, 77 }, { 19, 96 }, { 20, 13 }, { 20, 38 }, { 20, 52 },
			{ 20, 65 }, { 20, 77 }, { 20, 88 }, { 20, 99 }, { 20, 114 },
			{ 20, 126 }, { 21, 1 }, { 21, 11 }, { 21, 25 }, { 21, 36 },
			{ 21, 45 }, { 21, 58 }, { 21, 73 }, { 21, 82 }, { 21, 91 },
			{ 21, 102 }, { 22, 1 }, { 22, 6 }, { 22, 16 }, { 22, 24 },
			{ 22, 31 }, { 22, 39 }, { 22, 47 }, { 22, 56 }, { 22, 65 },
			{ 22, 73 }, { 23, 1 }, { 23, 18 }, { 23, 28 }, { 23, 43 },
			{ 23, 60 }, { 23, 75 }, { 23, 90 }, { 23, 105 }, { 24, 1 },
			{ 24, 11 }, { 24, 21 }, { 24, 28 }, { 24, 32 }, { 24, 37 },
			{ 24, 44 }, { 24, 54 }, { 24, 59 }, { 24, 62 }, { 25, 3 },
			{ 25, 12 }, { 25, 21 }, { 25, 33 }, { 25, 44 }, { 25, 56 },
			{ 25, 68 }, { 26, 1 }, { 26, 20 }, { 26, 40 }, { 26, 61 },
			{ 26, 84 }, { 26, 112 }, { 26, 137 }, { 26, 160 }, { 26, 184 },
			{ 26, 207 }, { 27, 1 }, { 27, 14 }, { 27, 23 }, { 27, 36 },
			{ 27, 45 }, { 27, 56 }, { 27, 64 }, { 27, 77 }, { 27, 89 },
			{ 28, 6 }, { 28, 14 }, { 28, 22 }, { 28, 29 }, { 28, 36 },
			{ 28, 44 }, { 28, 51 }, { 28, 60 }, { 28, 71 }, { 28, 78 },
			{ 28, 85 }, { 29, 7 }, { 29, 15 }, { 29, 24 }, { 29, 31 },
			{ 29, 39 }, { 29, 46 }, { 29, 53 }, { 29, 64 }, { 30, 6 },
			{ 30, 16 }, { 30, 25 }, { 30, 33 }, { 30, 42 }, { 30, 51 },
			{ 31, 1 }, { 31, 12 }, { 31, 20 }, { 31, 29 }, { 32, 1 },
			{ 32, 12 }, { 32, 21 }, { 33, 1 }, { 33, 7 }, { 33, 16 },
			{ 33, 23 }, { 33, 31 }, { 33, 36 }, { 33, 44 }, { 33, 51 },
			{ 33, 55 }, { 33, 63 }, { 34, 1 }, { 34, 8 }, { 34, 15 },
			{ 34, 23 }, { 34, 32 }, { 34, 40 }, { 34, 49 }, { 35, 4 },
			{ 35, 12 }, { 35, 19 }, { 35, 31 }, { 35, 39 }, { 35, 45 },
			{ 36, 13 }, { 36, 28 }, { 36, 41 }, { 36, 55 }, { 36, 71 },
			{ 37, 1 }, { 37, 25 }, { 37, 52 }, { 37, 77 }, { 37, 103 },
			{ 37, 127 }, { 37, 154 }, { 38, 1 }, { 38, 17 }, { 38, 27 },
			{ 38, 43 }, { 38, 62 }, { 38, 84 }, { 39, 6 }, { 39, 11 },
			{ 39, 22 }, { 39, 32 }, { 39, 41 }, { 39, 48 }, { 39, 57 },
			{ 39, 68 }, { 39, 75 }, { 40, 8 }, { 40, 17 }, { 40, 26 },
			{ 40, 34 }, { 40, 41 }, { 40, 50 }, { 40, 59 }, { 40, 67 },
			{ 40, 78 }, { 41, 1 }, { 41, 12 }, { 41, 21 }, { 41, 30 },
			{ 41, 39 }, { 41, 47 }, { 42, 1 }, { 42, 11 }, { 42, 16 },
			{ 42, 23 }, { 42, 32 }, { 42, 45 }, { 42, 52 }, { 43, 11 },
			{ 43, 23 }, { 43, 34 }, { 43, 48 }, { 43, 61 }, { 43, 74 },
			{ 44, 1 }, { 44, 19 }, { 44, 40 }, { 45, 1 }, { 45, 14 },
			{ 45, 23 }, { 45, 33 }, { 46, 6 }, { 46, 15 }, { 46, 21 },
			{ 46, 29 }, { 47, 1 }, { 47, 12 }, { 47, 20 }, { 47, 30 },
			{ 48, 1 }, { 48, 10 }, { 48, 16 }, { 48, 24 }, { 48, 29 },
			{ 49, 5 }, { 49, 12 }, { 50, 1 }, { 50, 16 }, { 50, 36 },
			{ 51, 7 }, { 51, 31 }, { 51, 52 }, { 52, 15 }, { 52, 32 },
			{ 53, 1 }, { 53, 27 }, { 53, 45 }, { 54, 7 }, { 54, 28 },
			{ 54, 50 }, { 55, 17 }, { 55, 41 }, { 55, 68 }, { 56, 17 },
			{ 56, 51 }, { 56, 77 }, { 57, 4 }, { 57, 12 }, { 57, 19 },
			{ 57, 25 }, { 58, 1 }, { 58, 7 }, { 58, 12 }, { 58, 22 },
			{ 59, 4 }, { 59, 10 }, { 59, 17 }, { 60, 1 }, { 60, 6 },
			{ 60, 12 }, { 61, 6 }, { 62, 1 }, { 62, 9 }, { 63, 5 },
			{ 64, 1 }, { 64, 10 }, { 65, 1 }, { 65, 6 }, { 66, 1 },
			{ 66, 8 }, { 67, 1 }, { 67, 13 }, { 67, 27 }, { 68, 16 },
			{ 68, 43 }, { 69, 9 }, { 69, 35 }, { 70, 11 }, { 70, 40 },
			{ 71, 11 }, { 72, 1 }, { 72, 14 }, { 73, 1 }, { 73, 20 },
			{ 74, 18 }, { 74, 48 }, { 75, 20 }, { 76, 6 }, { 76, 26 },
			{ 77, 20 }, { 78, 1 }, { 78, 31 }, { 79, 16 }, { 80, 1 },
			{ 81, 1 }, { 82, 1 }, { 83, 7 }, { 83, 35 }, { 85, 1 },
			{ 86, 1 }, { 87, 16 }, { 89, 1 }, { 89, 24 }, { 91, 1 },
			{ 92, 15 }, { 95, 1 }, { 97, 1 }, { 98, 8 }, { 100, 10 },
			{ 103, 1 }, { 106, 1 }, { 109, 1 }, { 112, 1 }
	};

	public int getSuraCount() {
		return mSuras.length;
	}

	public Sura getSura(int sura) {
		return mSuras[sura - 1];
	}

	public int getMarkCount(int mode) {
		if (mode == Paging.SURA) {
			return mSuras.length;
		} else if (mode == Paging.PAGE) {
			return mPages.length;
		} else if (mode == Paging.JUZ) {
			return mJuzs.length;
		} else {
			return mHizbs.length;
		}
	}

	public Mark getMarkStart(int mode, int index) {
		if (mode == Paging.SURA) {
			return new Mark(index, 1);
		}

		int[] mark = null;
		if (mode == Paging.PAGE) {
			mark = mPages[index - 1];
		} else if (mode == Paging.JUZ) {
			mark = mJuzs[index - 1];
		} else if (mode == Paging.HIZB) {
			mark = mHizbs[index - 1];
		}
		return new Mark(mark[0], mark[1]);
	}

	public Mark getMarkEnd(int mode, int index) {
		if (mode == Paging.SURA) {
			return new Mark(index, mSuras[index - 1].ayas);
		}

		int[] mark = { 115, 1 };
		if (mode == Paging.PAGE) {
			mark = index < mPages.length ? mPages[index] : mark;
		} else if (mode == Paging.JUZ) {
			mark = index < mJuzs.length ? mJuzs[index] : mark;
		} else if (mode == Paging.HIZB) {
			mark = index < mHizbs.length ? mHizbs[index] : mark;
		}

		if (mark[1] == 1) {
			return new Mark(mark[0] - 1, mSuras[mark[0] - 2].ayas);
		}
		return new Mark(mark[0], mark[1] - 1);
	}

	public int find(int mode, int sura, int aya) {
		if (mode == Paging.SURA) {
			return sura;
		}

		int[][] marks = null;
		if (mode == Paging.PAGE) {
			marks = mPages;
		} else if (mode == Paging.JUZ) {
			marks = mJuzs;
		} else if (mode == Paging.HIZB) {
			marks = mHizbs;
		}
		for (int i = 1; i < marks.length; i++) {
			if (sura < marks[i][0] || (sura == marks[i][0] && aya < marks[i][1])) {
				return i;
			}
		}
		return marks.length;
	}

}

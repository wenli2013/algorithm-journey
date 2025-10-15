package class178;

// 区间倍数二元组，java版
// 给定一个长度为n的数组arr，下面给出倍数二元组的定义
// 如果arr[i]是arr[j]的倍数(>=1倍)，那么(i, j)就是一个倍数二元组
// 当i != j时，(i, j)和(j, i)认为是不同的二元组，不要漏算
// 当i == j时，(i, j)和(j, i)认为是相同的二元组，不要多算
// 比如[2, 4, 2, 6]，有10个倍数二元组
// 一共有m条查询，格式为 l r : 打印arr[l..r]范围上，有多少倍数二元组
// 1 <= n、m、arr[i] <= 5 * 10^5
// 测试链接 : https://www.luogu.com.cn/problem/P5398
// 提交以下的code，提交时请把类名改成"Main"
// java实现的逻辑一定是正确的，但是本题卡常，无法通过所有测试用例
// 想通过用C++实现，本节课Code04_Gosick2文件就是C++的实现
// 两个版本的逻辑完全一样，C++版本可以通过所有测试

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

public class Code04_Gosick1 {

	public static int MAXN = 500001;
	public static int MAXF = 5000001;
	public static int LIMIT = 100;
	public static int n, m, maxv;
	public static int[] arr = new int[MAXN];
	public static int[] bi = new int[MAXN];

	// 每个数的因子表，用链式前向星表达
	public static int[] headf = new int[MAXN];
	public static int[] nextf = new int[MAXF];
	public static int[] fac = new int[MAXF];
	public static int cntf;

	// 莫队任务
	public static int[][] query = new int[MAXN][3];

	// 二次离线的任务，也是链式前向星
	public static int[] headq = new int[MAXN];
	public static int[] nextq = new int[MAXN << 1];
	public static int[] qx = new int[MAXN << 1];
	public static int[] ql = new int[MAXN << 1];
	public static int[] qr = new int[MAXN << 1];
	public static int[] qop = new int[MAXN << 1];
	public static int[] qid = new int[MAXN << 1];
	public static int cntq;

	// xcnt[v] = 之前出现的数中，有多少数是此时数字v的倍数
	public static int[] xcnt = new int[MAXN];
	// vcnt[v] = 之前出现的数中，数字v出现了多少次
	public static int[] vcnt = new int[MAXN];
	// 前缀信息
	public static long[] pre = new long[MAXN];

	public static long[] ans = new long[MAXN];

	public static class QueryCmp implements Comparator<int[]> {
		@Override
		public int compare(int[] a, int[] b) {
			if (bi[a[0]] != bi[b[0]]) {
				return bi[a[0]] - bi[b[0]];
			}
			return a[1] - b[1];
		}
	}

	public static void addFactors(int num) {
		if (headf[num] == 0) {
			for (int f = 1; f * f <= num; f++) {
				if (num % f == 0) {
					nextf[++cntf] = headf[num];
					fac[cntf] = f;
					headf[num] = cntf;
				}
			}
		}
	}

	public static void addOffline(int x, int l, int r, int op, int id) {
		nextq[++cntq] = headq[x];
		headq[x] = cntq;
		qx[cntq] = x;
		ql[cntq] = l;
		qr[cntq] = r;
		qop[cntq] = op;
		qid[cntq] = id;
	}

	public static void compute() {
		for (int i = 1; i <= n; i++) {
			int num = arr[i];
			pre[i] = pre[i - 1];
			pre[i] += xcnt[num];
			for (int e = headf[num], f, other; e > 0; e = nextf[e]) {
				f = fac[e];
				other = num / f;
				xcnt[f]++;
				pre[i] += vcnt[f];
				if (other != f) {
					xcnt[other]++;
					pre[i] += vcnt[other];
				}
			}
			vcnt[num]++;
		}
		// 第一次离线，执行莫队
		int winl = 1, winr = 0;
		for (int i = 1; i <= m; i++) {
			int jobl = query[i][0];
			int jobr = query[i][1];
			int id = query[i][2];
			if (winr < jobr) {
				addOffline(winl - 1, winr + 1, jobr, -1, id);
				ans[id] += pre[jobr] - pre[winr];
			}
			if (winr > jobr) {
				addOffline(winl - 1, jobr + 1, winr, 1, id);
				ans[id] -= pre[winr] - pre[jobr];
			}
			winr = jobr;
			// 接下来是winl滑动
			// 课上重点图解了，需要考虑 (2 * 滑动长度) 的修正
			if (winl > jobl) {
				addOffline(winr, jobl, winl - 1, 1, id);
				ans[id] -= pre[winl - 1] - pre[jobl - 1] + 2 * (winl - jobl);
			}
			if (winl < jobl) {
				addOffline(winr, winl, jobl - 1, -1, id);
				ans[id] += pre[jobl - 1] - pre[winl - 1] + 2 * (jobl - winl);
			}
			winl = jobl;
		}
		// 第二次离线，num倍数的数量 + num一部分因子的数量，都计入xcnt[num]
		Arrays.fill(xcnt, 0);
		for (int x = 0; x <= n; x++) {
			if (x >= 1) {
				int num = arr[x];
				for (int e = headf[num], f, other; e > 0; e = nextf[e]) {
					f = fac[e];
					other = num / f;
					xcnt[f]++;
					if (other != f) {
						xcnt[other]++;
					}
				}
				// 只处理大于LIMIT值的num
				if (num > LIMIT) {
					for (int v = num; v <= maxv; v += num) {
						xcnt[v]++;
					}
				}
			}
			for (int q = headq[x]; q > 0; q = nextq[q]) {
				int l = ql[q], r = qr[q], op = qop[q], id = qid[q];
				for (int j = l; j <= r; j++) {
					ans[id] += (long) op * xcnt[arr[j]];
				}
			}
		}
		// 第三次离线，1 ~ LIMIT 这些因子是少算了的，如今补回来
		for (int v = 1; v <= LIMIT; v++) {
			// 复用vcnt和xcnt
			// vcnt[i]，当前表示，1~i范围上，v出现的次数
			// xcnt[i]，当前表示，1~i范围上，v的倍数出现的次数
			vcnt[0] = xcnt[0] = 0;
			for (int i = 1; i <= n; i++) {
				vcnt[i] = vcnt[i - 1] + (arr[i] == v ? 1 : 0);
				xcnt[i] = xcnt[i - 1] + (arr[i] % v == 0 ? 1 : 0);
			}
			for (int i = 1; i <= cntq; i++) {
				int x = qx[i], l = ql[i], r = qr[i], op = qop[i], id = qid[i];
				ans[id] += (long) op * vcnt[x] * (xcnt[r] - xcnt[l - 1]);
			}
		}
	}

	public static void prepare() {
		int blen = (int) Math.sqrt(n);
		for (int i = 1; i <= n; i++) {
			bi[i] = (i - 1) / blen + 1;
			maxv = Math.max(maxv, arr[i]);
			addFactors(arr[i]);
		}
		Arrays.sort(query, 1, m + 1, new QueryCmp());
	}

	public static void main(String[] args) throws Exception {
		FastReader in = new FastReader(System.in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		m = in.nextInt();
		for (int i = 1; i <= n; i++) {
			arr[i] = in.nextInt();
		}
		for (int i = 1; i <= m; i++) {
			query[i][0] = in.nextInt();
			query[i][1] = in.nextInt();
			query[i][2] = i;
		}
		prepare();
		compute();
		// 答案变化量生成前缀和
		for (int i = 2; i <= m; i++) {
			ans[query[i][2]] += ans[query[i - 1][2]];
		}
		// 贡献是重新定义的，答案需要补偿回来
		for (int i = 1; i <= m; i++) {
			ans[query[i][2]] += query[i][1] - query[i][0] + 1;
		}
		for (int i = 1; i <= m; i++) {
			out.println(ans[i]);
		}
		out.flush();
		out.close();
	}

	// 读写工具类
	static class FastReader {
		private final byte[] buffer = new byte[1 << 16];
		private int ptr = 0, len = 0;
		private final InputStream in;

		FastReader(InputStream in) {
			this.in = in;
		}

		private int readByte() throws IOException {
			if (ptr >= len) {
				len = in.read(buffer);
				ptr = 0;
				if (len <= 0)
					return -1;
			}
			return buffer[ptr++];
		}

		int nextInt() throws IOException {
			int c;
			do {
				c = readByte();
			} while (c <= ' ' && c != -1);
			boolean neg = false;
			if (c == '-') {
				neg = true;
				c = readByte();
			}
			int val = 0;
			while (c > ' ' && c != -1) {
				val = val * 10 + (c - '0');
				c = readByte();
			}
			return neg ? -val : val;
		}
	}

}

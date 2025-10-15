package class167;

// 博物馆劫案，java版
// 给定n件商品，商品有价值v和重量w，1~n号商品加入集合s
// 接下来有q个操作，每种操作是如下三种类型中的一种
// 操作 1 x y : 集合s中增加价值x、重量y的商品，商品编号自增得到
// 操作 2 x   : 集合s中删除编号为x的商品，删除时保证x号商品存在
// 操作 3     : 查询当前的f(s)
// 定义a(s, m) = 集合s中，挑选商品总重量<=m，能获得的最大价值
// 给定正数k、BAS、MOD，定义f(s) = ∑(m = 1...k) (a(s, m) * BAS的m-1次方 % MOD)
// 1 <= n <= 5 * 10^3    1 <= q <= 3 * 10^4
// 1 <= k、每件商品重量 <= 10^3    1 <= 每件商品价值 <= 10^6
// 测试链接 : https://www.luogu.com.cn/problem/CF601E
// 测试链接 : https://codeforces.com/problemset/problem/601/E
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Code01_MuseumRobbery1 {

	public static int MAXN = 40001;
	public static int MAXQ = 30001;
	public static int MAXK = 1001;
	public static int MAXT = 1000001;
	public static int DEEP = 20;
	public static int BAS = 10000019;
	public static int MOD = 1000000007;
	public static int n, k, q;

	public static int[] v = new int[MAXN];
	public static int[] w = new int[MAXN];

	public static int[] op = new int[MAXQ];
	public static int[] x = new int[MAXQ];
	public static int[] y = new int[MAXQ];

	// 第i号商品的生效时间段，从from[i]持续到to[i]
	public static int[] from = new int[MAXN];
	public static int[] to = new int[MAXN];

	// 时间轴线段树的区间上挂上生效的商品，价值v，重量w
	public static int[] head = new int[MAXQ << 2];
	public static int[] next = new int[MAXT];
	public static int[] tov = new int[MAXT];
	public static int[] tow = new int[MAXT];
	public static int cnt = 0;

	// 动态规划表
	public static long[] dp = new long[MAXK];
	// 动态规划表的备份
	public static long[][] backup = new long[DEEP][MAXK];

	// 答案
	public static long[] ans = new long[MAXQ];

	public static void clone(long[] a, long[] b) {
		for (int i = 0; i <= k; i++) {
			a[i] = b[i];
		}
	}

	public static void addEdge(int i, int v, int w) {
		next[++cnt] = head[i];
		tov[cnt] = v;
		tow[cnt] = w;
		head[i] = cnt;
	}

	public static void add(int jobl, int jobr, int jobv, int jobw, int l, int r, int i) {
		if (jobl <= l && r <= jobr) {
			addEdge(i, jobv, jobw);
		} else {
			int mid = (l + r) >> 1;
			if (jobl <= mid) {
				add(jobl, jobr, jobv, jobw, l, mid, i << 1);
			}
			if (jobr > mid) {
				add(jobl, jobr, jobv, jobw, mid + 1, r, i << 1 | 1);
			}
		}
	}

	public static void dfs(int l, int r, int i, int dep) {
		clone(backup[dep], dp);
		for (int e = head[i]; e > 0; e = next[e]) {
			int v = tov[e];
			int w = tow[e];
			for (int j = k; j >= w; j--) {
				dp[j] = Math.max(dp[j], dp[j - w] + v);
			}
		}
		if (l == r) {
			if (op[l] == 3) {
				long ret = 0;
				long base = 1;
				for (int j = 1; j <= k; j++) {
					ret = (ret + dp[j] * base) % MOD;
					base = (base * BAS) % MOD;
				}
				ans[l] = ret;
			}
		} else {
			int mid = (l + r) >> 1;
			dfs(l, mid, i << 1, dep + 1);
			dfs(mid + 1, r, i << 1 | 1, dep + 1);
		}
		clone(dp, backup[dep]);
	}

	public static void prepare() {
		for (int i = 1; i <= n; i++) {
			from[i] = 1;
			to[i] = q;
		}
		for (int i = 1; i <= q; i++) {
			if (op[i] == 1) {
				n++;
				v[n] = x[i];
				w[n] = y[i];
				from[n] = i;
				to[n] = q;
			} else if (op[i] == 2) {
				to[x[i]] = i - 1;
			}
		}
		for (int i = 1; i <= n; i++) {
			if (from[i] <= to[i]) {
				add(from[i], to[i], v[i], w[i], 1, q, 1);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		FastReader in = new FastReader(System.in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		k = in.nextInt();
		for (int i = 1; i <= n; i++) {
			v[i] = in.nextInt();
			w[i] = in.nextInt();
		}
		q = in.nextInt();
		for (int i = 1; i <= q; i++) {
			op[i] = in.nextInt();
			if (op[i] == 1) {
				x[i] = in.nextInt();
				y[i] = in.nextInt();
			} else if (op[i] == 2) {
				x[i] = in.nextInt();
			}
		}
		prepare();
		dfs(1, q, 1, 1);
		for (int i = 1; i <= q; i++) {
			if (op[i] == 3) {
				out.println(ans[i]);
			}
		}
		out.flush();
		out.close();
	}

	// 读写工具类
	static class FastReader {
		private final byte[] buffer = new byte[1 << 20];
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

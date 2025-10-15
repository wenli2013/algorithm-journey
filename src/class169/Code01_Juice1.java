package class169;

// 混合果汁，java版
// 一共有n种果汁，每种果汁给定，美味度d、每升价格p、添加上限l
// 制作混合果汁时每种果汁不能超过添加上限，其中美味度最低的果汁，决定混合果汁的美味度
// 一共有m个小朋友，给每位制作混合果汁时，钱数不超过money[i]，体积不少于least[i]
// 打印每个小朋友能得到的混合果汁最大美味度，如果无法满足，打印-1
// 1 <= n、m、d、p、l <= 10^5
// 1 <= money[i]、least[i] <= 10^18
// 测试链接 : https://www.luogu.com.cn/problem/P4602
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class Code01_Juice1 {

	public static int MAXN = 100001;
	public static int n, m;
	// 果汁有三个参数，美味度d、每升价格p、添加上限l
	public static int[][] juice = new int[MAXN][3];
	// 记录所有小朋友的编号
	public static int[] qid = new int[MAXN];
	// 小朋友能花的钱数
	public static long[] money = new long[MAXN];
	// 小朋友至少的果汁量
	public static long[] least = new long[MAXN];

	// 这种使用线段树的方式叫线段树二分
	// 讲解146的题目2，也涉及线段树二分
	// 果汁单价作为下标的线段树
	// maxp为最大的果汁单价
	public static int maxp = 0;
	// suml[i] : 线段树某单价区间上，允许添加的总量
	public static long[] suml = new long[MAXN << 2];
	// cost[i] : 线段树某单价区间上，如果允许添加的总量全要，花费多少钱
	public static long[] cost = new long[MAXN << 2];
	// 多少种果汁加入了线段树
	public static int used = 0;

	// 整体二分的过程需要
	public static int[] lset = new int[MAXN];
	public static int[] rset = new int[MAXN];

	// 每个小朋友的答案，是第几号果汁的美味度
	public static int[] ans = new int[MAXN];

	public static void up(int i) {
		suml[i] = suml[i << 1] + suml[i << 1 | 1];
		cost[i] = cost[i << 1] + cost[i << 1 | 1];
	}

	// 单价为jobi，现在允许添加的量增加了jobv
	public static void add(int jobi, int jobv, int l, int r, int i) {
		if (l == r) {
			suml[i] += jobv;
			cost[i] = suml[i] * l;
		} else {
			int mid = (l + r) >> 1;
			if (jobi <= mid) {
				add(jobi, jobv, l, mid, i << 1);
			} else {
				add(jobi, jobv, mid + 1, r, i << 1 | 1);
			}
			up(i);
		}
	}

	// 总体积一共volume，已知总体积一定能耗尽
	// 返回总体积耗尽的情况下，能花的最少钱数
	public static long query(long volume, int l, int r, int i) {
		if (l == r) {
			return volume * l;
		}
		int mid = (l + r) >> 1;
		if (suml[i << 1] >= volume) {
			return query(volume, l, mid, i << 1);
		} else {
			return cost[i << 1] + query(volume - suml[i << 1], mid + 1, r, i << 1 | 1);
		}
	}

	public static void compute(int ql, int qr, int vl, int vr) {
		if (ql > qr) {
			return;
		}
		if (vl == vr) {
			for (int i = ql; i <= qr; i++) {
				ans[qid[i]] = vl;
			}
		} else {
			int mid = (vl + vr) >> 1;
			// 线段树包含果汁的种类少就添加
			while (used < mid) {
				used++;
				add(juice[used][1], juice[used][2], 1, maxp, 1);
			}
			// 线段树包含果汁的种类多就撤销
			while (used > mid) {
				add(juice[used][1], -juice[used][2], 1, maxp, 1);
				used--;
			}
			int lsiz = 0, rsiz = 0;
			for (int i = ql, id; i <= qr; i++) {
				id = qid[i];
				if (suml[1] >= least[id] && query(least[id], 1, maxp, 1) <= money[id]) {
					lset[++lsiz] = id;
				} else {
					rset[++rsiz] = id;
				}
			}
			for (int i = 1; i <= lsiz; i++) {
				qid[ql + i - 1] = lset[i];
			}
			for (int i = 1; i <= rsiz; i++) {
				qid[ql + lsiz + i - 1] = rset[i];
			}
			compute(ql, ql + lsiz - 1, vl, mid);
			compute(ql + lsiz, qr, mid + 1, vr);
		}
	}

	public static void main(String[] args) throws Exception {
		FastReader in = new FastReader(System.in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		m = in.nextInt();
		for (int i = 1; i <= n; i++) {
			juice[i][0] = in.nextInt();
			juice[i][1] = in.nextInt();
			juice[i][2] = in.nextInt();
			maxp = Math.max(maxp, juice[i][1]);
		}
		for (int i = 1; i <= m; i++) {
			qid[i] = i;
			money[i] = in.nextLong();
			least[i] = in.nextLong();
		}
		// 所有果汁按照美味度排序，美味度大的在前，美味度小的在后
		Arrays.sort(juice, 1, n + 1, (a, b) -> b[0] - a[0]);
		// 答案范围就是美味度范围，从最美味的第1名，到最难喝的第n名
		// 如果小朋友答案为n+1，说明无法满足这个小朋友
		compute(1, m, 1, n + 1);
		for (int i = 1; i <= m; i++) {
			if (ans[i] == n + 1) {
				out.println(-1);
			} else {
				out.println(juice[ans[i]][0]);
			}
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

		long nextLong() throws IOException {
			int c;
			do {
				c = readByte();
			} while (c <= ' ' && c != -1);
			boolean neg = false;
			if (c == '-') {
				neg = true;
				c = readByte();
			}
			long val = 0L;
			while (c > ' ' && c != -1) {
				val = val * 10 + (c - '0');
				c = readByte();
			}
			return neg ? -val : val;
		}

	}

}

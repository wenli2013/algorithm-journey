package class179;

// 掉进兔子洞，java版
// 三个区间同时出现的数，一个一个删掉，直到无法再删，剩下数字的个数叫做 剩余个数
// A = [1 2 2 3 3 3]    B = [1 2 2 3 3 3]    C = [1 1 2 3 3]
// 删除的过程为，一起删掉一个1、一起删掉一个2、一起删掉2个3，然后状况为
// A = [2 3]    B = [2 3]    C = [1]    剩余个数为5
// 给定一个长度为n的数组arr，下来有m条查询，格式如下
// 查询 l1 r1 l2 r2 l3 r3 : 给定了三个区间，打印剩余个数
// 1 <= n、m <= 10^5
// 1 <= arr[i] <= 10^9
// 测试链接 : https://www.luogu.com.cn/problem/P4688
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

public class Code07_RabbitHole1 {

	static class BitSet {
		int len;
		long[] status;

		public BitSet(int siz) {
			len = (siz + 63) >> 6;
			status = new long[len];
		}

		public void clear() {
			for (int i = 0; i < len; i++) {
				status[i] = 0;
			}
		}

		public void copy(BitSet obj) {
			for (int i = 0; i < len; i++) {
				status[i] = obj.status[i];
			}
		}

		public void and(BitSet obj) {
			for (int i = 0; i < len; i++) {
				status[i] &= obj.status[i];
			}
		}

		public void setOne(int bit) {
			status[bit >> 6] |= 1L << (bit & 63);
		}

		public void setZero(int bit) {
			status[bit >> 6] &= ~(1L << (bit & 63));
		}

		public int getOnes() {
			int ret = 0;
			for (int i = 0; i < len; i++) {
				ret += Long.bitCount(status[i]);
			}
			return ret;
		}

	}

	public static int MAXN = 100001;
	public static int MAXT = 30001;
	public static int n, m;
	public static int[] arr = new int[MAXN];
	public static int[][] query = new int[MAXT * 3][3];

	// 排序之后不去重
	// 得到每个数字的排名
	public static int[] sorted = new int[MAXN];
	public static int[] bi = new int[MAXN];

	// cnt[v] = c，表示窗口中数字v出现c次
	public static int[] cnt = new int[MAXN];
	// 当前窗口的位图
	public static BitSet curSet;
	// 问题从来没获得位图就拷贝，问题获得过位图就做&运算
	public static boolean[] hasSet = new boolean[MAXT];
	// 每个问题的位图
	public static BitSet[] bitSet = new BitSet[MAXT];

	public static int[] ans = new int[MAXT];

	public static class QueryCmp implements Comparator<int[]> {
		@Override
		public int compare(int[] a, int[] b) {
			if (bi[a[0]] != bi[b[0]]) {
				return bi[a[0]] - bi[b[0]];
			}
			if ((bi[a[0]] & 1) == 1) {
				return a[1] - b[1];
			} else {
				return b[1] - a[1];
			}
		}
	}

	public static int kth(int num) {
		int left = 1, right = n, mid, ret = -1;
		while (left <= right) {
			mid = (left + right) >> 1;
			if (sorted[mid] >= num) {
				ret = mid;
				right = mid - 1;
			} else {
				left = mid + 1;
			}
		}
		return ret;
	}

	public static void add(int x) {
		cnt[x]++;
		curSet.setOne(x + cnt[x] - 1);
	}

	public static void del(int x) {
		cnt[x]--;
		curSet.setZero(x + cnt[x]);
	}

	public static void compute(int q) {
		int winl = 1, winr = 0;
		for (int i = 1; i <= q; i++) {
			int jobl = query[i][0];
			int jobr = query[i][1];
			int id = query[i][2];
			while (winl > jobl) {
				add(arr[--winl]);
			}
			while (winr < jobr) {
				add(arr[++winr]);
			}
			while (winl < jobl) {
				del(arr[winl++]);
			}
			while (winr > jobr) {
				del(arr[winr--]);
			}
			if (!hasSet[id]) {
				hasSet[id] = true;
				bitSet[id].copy(curSet);
			} else {
				bitSet[id].and(curSet);
			}
		}
	}

	public static void prepare() {
		for (int i = 1; i <= n; i++) {
			sorted[i] = arr[i];
		}
		Arrays.sort(sorted, 1, n + 1);
		for (int i = 1; i <= n; i++) {
			arr[i] = kth(arr[i]);
		}
		int blen = (int) Math.sqrt(n);
		for (int i = 1; i <= n; i++) {
			bi[i] = (i - 1) / blen + 1;
		}
		for (int i = 1; i < MAXT; i++) {
			bitSet[i] = new BitSet(n + 1);
		}
		curSet = new BitSet(n + 1);
	}

	public static void main(String[] args) throws Exception {
		FastReader in = new FastReader(System.in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		m = in.nextInt();
		for (int i = 1; i <= n; i++) {
			arr[i] = in.nextInt();
		}
		prepare();
		for (int t = MAXT - 1; m > 0; m -= t) {
			int k = Math.min(m, t);
			Arrays.fill(cnt, 1, n + 1, 0);
			Arrays.fill(hasSet, 1, k + 1, false);
			Arrays.fill(ans, 1, k + 1, 0);
			curSet.clear();
			int cntq = 0;
			for (int i = 1; i <= k; i++) {
				for (int j = 1; j <= 3; j++) {
					query[++cntq][0] = in.nextInt();
					query[cntq][1] = in.nextInt();
					query[cntq][2] = i;
					ans[i] += query[cntq][1] - query[cntq][0] + 1;
				}
			}
			Arrays.sort(query, 1, cntq + 1, new QueryCmp());
			compute(cntq);
			for (int i = 1; i <= k; i++) {
				ans[i] -= bitSet[i].getOnes() * 3;
			}
			for (int i = 1; i <= k; i++) {
				out.println(ans[i]);
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
	}

}

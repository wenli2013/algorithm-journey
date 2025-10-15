package class178;

// 区间逆序对，java版
// 给定一个长度为n的数组arr，如果i < j，并且arr[i] > arr[j]，那么(i,j)就是逆序对
// 一共有m条查询，格式为 l r : 打印arr[l..r]范围上，逆序对的数量
// 1 <= n、m <= 10^5
// 0 <= arr[i] <= 10^9
// 本题允许离线，讲解173，题目4，讲了在线查询区间逆序对，但是给定的数组为排列
// 测试链接 : https://www.luogu.com.cn/problem/P5047
// 提交以下的code，提交时请把类名改成"Main"
// java实现的逻辑一定是正确的，但是本题卡常，无法通过所有测试用例
// 想通过用C++实现，本节课Code02_OfflineInversion2文件就是C++的实现
// 两个版本的逻辑完全一样，C++版本可以通过所有测试

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

public class Code02_OfflineInversion1 {

	public static int MAXN = 100002;
	public static int MAXB = 401;
	public static int n, m;
	public static int[] arr = new int[MAXN];
	public static int[] sorted = new int[MAXN];
	public static int cntv;

	public static int[][] query = new int[MAXN][3];
	public static int[] headl = new int[MAXN];
	public static int[] headr = new int[MAXN];
	public static int[] nextq = new int[MAXN << 1];
	public static int[] ql = new int[MAXN << 1];
	public static int[] qr = new int[MAXN << 1];
	public static int[] qop = new int[MAXN << 1];
	public static int[] qid = new int[MAXN << 1];
	public static int cntq;

	// bi用于序列分块、值域分块，bl和br用于值域分块
	public static int[] bi = new int[MAXN];
	public static int[] bl = new int[MAXB];
	public static int[] br = new int[MAXB];

	// 树状数组
	public static int[] tree = new int[MAXN];
	// 前缀信息
	public static long[] pre = new long[MAXN];
	// 后缀信息
	public static long[] suf = new long[MAXN];

	// 整块增加的词频
	public static long[] blockCnt = new long[MAXB];
	// 单个数值的词频
	public static long[] numCnt = new long[MAXN];

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

	public static int kth(int num) {
		int left = 1, right = cntv, mid, ret = 0;
		while (left <= right) {
			mid = (left + right) / 2;
			if (sorted[mid] <= num) {
				ret = mid;
				left = mid + 1;
			} else {
				right = mid - 1;
			}
		}
		return ret;
	}

	public static int lowbit(int i) {
		return i & -i;
	}

	public static void add(int i, int v) {
		while (i <= cntv) {
			tree[i] += v;
			i += lowbit(i);
		}
	}

	public static int sum(int i) {
		int ret = 0;
		while (i > 0) {
			ret += tree[i];
			i -= lowbit(i);
		}
		return ret;
	}

	public static void addLeftOffline(int x, int l, int r, int op, int id) {
		nextq[++cntq] = headl[x];
		headl[x] = cntq;
		ql[cntq] = l;
		qr[cntq] = r;
		qop[cntq] = op;
		qid[cntq] = id;
	}

	public static void addRightOffline(int x, int l, int r, int op, int id) {
		nextq[++cntq] = headr[x];
		headr[x] = cntq;
		ql[cntq] = l;
		qr[cntq] = r;
		qop[cntq] = op;
		qid[cntq] = id;
	}

	// 增加1 ~ val-1，这些数字的词频
	public static void addLeftCnt(int val) {
		for (int b = 1; b <= bi[val] - 1; b++) {
			blockCnt[b]++;
		}
		for (int i = bl[bi[val]]; i < val; i++) {
			numCnt[i]++;
		}
	}

	// 增加val+1 ~ cntv，这些数字的词频
	public static void addRightCnt(int val) {
		for (int b = bi[val] + 1; b <= bi[cntv]; b++) {
			blockCnt[b]++;
		}
		for (int i = val + 1; i <= br[bi[val]]; i++) {
			numCnt[i]++;
		}
	}

	public static long getCnt(int val) {
		return blockCnt[bi[val]] + numCnt[val];
	}

	public static void prepare() {
		for (int i = 1; i <= n; i++) {
			sorted[i] = arr[i];
		}
		Arrays.sort(sorted, 1, n + 1);
		cntv = 1;
		for (int i = 2; i <= n; i++) {
			if (sorted[cntv] != sorted[i]) {
				sorted[++cntv] = sorted[i];
			}
		}
		for (int i = 1; i <= n; i++) {
			arr[i] = kth(arr[i]);
		}
		int blen = (int) Math.sqrt(n);
		int bnum = (n + blen - 1) / blen;
		for (int i = 1; i <= n; i++) {
			bi[i] = (i - 1) / blen + 1;
		}
		for (int i = 1; i <= bnum; i++) {
			bl[i] = (i - 1) * blen + 1;
			br[i] = Math.min(i * blen, cntv);
		}
		Arrays.sort(query, 1, m + 1, new QueryCmp());
	}

	public static void compute() {
		for (int i = 1; i <= n; i++) {
			pre[i] = pre[i - 1] + sum(cntv) - sum(arr[i]);
			add(arr[i], 1);
		}
		Arrays.fill(tree, 1, cntv + 1, 0);
		for (int i = n; i >= 1; i--) {
			suf[i] = suf[i + 1] + sum(arr[i] - 1);
			add(arr[i], 1);
		}
		int winl = 1, winr = 0;
		for (int i = 1; i <= m; i++) {
			int jobl = query[i][0];
			int jobr = query[i][1];
			int id = query[i][2];
			if (winr < jobr) {
				addLeftOffline(winl - 1, winr + 1, jobr, -1, id);
				ans[id] += pre[jobr] - pre[winr];
			}
			if (winr > jobr) {
				addLeftOffline(winl - 1, jobr + 1, winr, 1, id);
				ans[id] -= pre[winr] - pre[jobr];
			}
			winr = jobr;
			if (winl > jobl) {
				addRightOffline(winr + 1, jobl, winl - 1, -1, id);
				ans[id] += suf[jobl] - suf[winl];
			}
			if (winl < jobl) {
				addRightOffline(winr + 1, winl, jobl - 1, 1, id);
				ans[id] -= suf[winl] - suf[jobl];
			}
			winl = jobl;
		}
		for (int x = 0; x <= n; x++) {
			if (x >= 1) {
				addLeftCnt(arr[x]);
			}
			for (int q = headl[x]; q > 0; q = nextq[q]) {
				int l = ql[q], r = qr[q], op = qop[q], id = qid[q];
				long ret = 0;
				for (int j = l; j <= r; j++) {
					ret += getCnt(arr[j]);
				}
				ans[id] += ret * op;
			}
		}
		Arrays.fill(blockCnt, 0);
		Arrays.fill(numCnt, 0);
		for (int x = n + 1; x >= 1; x--) {
			if (x <= n) {
				addRightCnt(arr[x]);
			}
			for (int q = headr[x]; q > 0; q = nextq[q]) {
				int l = ql[q], r = qr[q], op = qop[q], id = qid[q];
				long ret = 0;
				for (int j = l; j <= r; j++) {
					ret += getCnt(arr[j]);
				}
				ans[id] += ret * op;
			}
		}
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
		for (int i = 2; i <= m; i++) {
			ans[query[i][2]] += ans[query[i - 1][2]];
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

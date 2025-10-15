package class171;

// 序列，java版
// 给定一个长度为n的数组arr，一共有m条操作，格式为 x v 表示x位置的数变成v
// 你可以选择不执行任何操作，或者只选择一个操作来执行，然后arr不再变动
// 请在arr中选出一组下标序列，不管你做出什么选择，下标序列所代表的数字都是不下降的
// 打印序列能达到的最大长度
// 1 <= 所有数字 <= 10^5
// 测试链接 : https://www.luogu.com.cn/problem/P4093
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class Code03_Sequence1 {

	public static int MAXN = 100001;
	public static int n, m;
	public static int[] v = new int[MAXN];
	public static int[] lv = new int[MAXN];
	public static int[] rv = new int[MAXN];

	// 位置i、数值v、最小值lv、最大值rv
	public static int[][] arr = new int[MAXN][4];
	// 树状数组维护前缀最大值
	public static int[] tree = new int[MAXN];
	public static int[] dp = new int[MAXN];

	public static int lowbit(int i) {
		return i & -i;
	}

	public static void more(int i, int num) {
		while (i <= n) {
			tree[i] = Math.max(tree[i], num);
			i += lowbit(i);
		}
	}

	public static int query(int i) {
		int ret = 0;
		while (i > 0) {
			ret = Math.max(ret, tree[i]);
			i -= lowbit(i);
		}
		return ret;
	}

	public static void clear(int i) {
		while (i <= n) {
			tree[i] = 0;
			i += lowbit(i);
		}
	}

	public static void merge(int l, int m, int r) {
		// 辅助数组arr拷贝l..r所有的对象
		// 接下来的排序都发生在arr中，不影响原始的次序
		for (int i = l; i <= r; i++) {
			arr[i][0] = i;
			arr[i][1] = v[i];
			arr[i][2] = lv[i];
			arr[i][3] = rv[i];
		}
		// 左侧根据v排序
		Arrays.sort(arr, l, m + 1, (a, b) -> a[1] - b[1]);
		// 右侧根据lv排序
		Arrays.sort(arr, m + 1, r + 1, (a, b) -> a[2] - b[2]);
		int p1, p2;
		for (p1 = l - 1, p2 = m + 1; p2 <= r; p2++) {
			// 左侧对象.v <= 右侧对象.lv 窗口扩充
			while (p1 + 1 <= m && arr[p1 + 1][1] <= arr[p2][2]) {
				p1++;
				// 树状数组中，下标是rv，加入的值是左侧对象的dp值
				more(arr[p1][3], dp[arr[p1][0]]);
			}
			// 右侧对象更新dp值，查出1..v范围上最大的dp值 + 1
			dp[arr[p2][0]] = Math.max(dp[arr[p2][0]], query(arr[p2][1]) + 1);
		}
		// 清空树状数组
		for (int i = l; i <= p1; i++) {
			clear(arr[i][3]);
		}
	}

	public static void cdq(int l, int r) {
		if (l == r) {
			return;
		}
		int mid = (l + r) / 2;
		cdq(l, mid);
		merge(l, mid, r);
		cdq(mid + 1, r);
	}

	public static void main(String[] args) throws IOException {
		FastReader in = new FastReader(System.in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		m = in.nextInt();
		for (int i = 1; i <= n; i++) {
			v[i] = in.nextInt();
			lv[i] = v[i];
			rv[i] = v[i];
		}
		for (int i = 1, idx, val; i <= m; i++) {
			idx = in.nextInt();
			val = in.nextInt();
			lv[idx] = Math.min(lv[idx], val);
			rv[idx] = Math.max(rv[idx], val);
		}
		for (int i = 1; i <= n; i++) {
			dp[i] = 1;
		}
		cdq(1, n);
		int ans = 0;
		for (int i = 1; i <= n; i++) {
			ans = Math.max(ans, dp[i]);
		}
		out.println(ans);
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

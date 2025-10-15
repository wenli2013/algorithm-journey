package class170;

// 园丁的烦恼，java版
// 有n棵树，每棵树给定位置坐标(x, y)，接下来有m条查询，格式如下
// 查询 a b c d : 打印左上角(a, b)、右下角(c, d)的区域里有几棵树
// 0 <= n <= 5 * 10^5
// 1 <= m <= 5 * 10^5
// 0 <= 坐标值 <= 10^7
// 测试链接 : https://www.luogu.com.cn/problem/P2163
// 提交以下的code，提交时请把类名改成"Main"
// java实现的逻辑一定是正确的，但无法通过测试用例，内存使用过大
// 因为这道题只考虑C++能通过的空间极限，根本没考虑java的用户
// 想通过用C++实现，本节课Code03_GardenerTrouble2文件就是C++的实现
// 两个版本的逻辑完全一样，C++版本可以通过所有测试

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class Code03_GardenerTrouble1 {

	public static int MAXN = 500001 * 5;
	public static int n, m;

	// op == 1代表树木，x、y
	// op == 2代表查询，x、y、效果v、查询编号q
	public static int[][] arr = new int[MAXN][5];
	public static int cnt = 0;

	// 归并排序需要
	public static int[][] tmp = new int[MAXN][5];

	// 问题的答案
	public static int[] ans = new int[MAXN];

	public static void clone(int[] a, int[] b) {
		a[0] = b[0];
		a[1] = b[1];
		a[2] = b[2];
		a[3] = b[3];
		a[4] = b[4];
	}

	public static void addTree(int x, int y) {
		arr[++cnt][0] = 1;
		arr[cnt][1] = x;
		arr[cnt][2] = y;
	}

	public static void addQuery(int x, int y, int v, int q) {
		arr[++cnt][0] = 2;
		arr[cnt][1] = x;
		arr[cnt][2] = y;
		arr[cnt][3] = v;
		arr[cnt][4] = q;
	}

	public static void merge(int l, int m, int r) {
		int p1, p2, tree = 0;
		for (p1 = l - 1, p2 = m + 1; p2 <= r; p2++) {
			while (p1 + 1 <= m && arr[p1 + 1][2] <= arr[p2][2]) {
				p1++;
				if (arr[p1][0] == 1) {
					tree++;
				}
			}
			if (arr[p2][0] == 2) {
				ans[arr[p2][4]] += tree * arr[p2][3];
			}
		}
		// 下面是经典归并的过程，为啥不直接排序了？
		// 因为没有用到高级数据结构，复杂度可以做到O(n * log n)
		// 那么就维持最好的复杂度，不用排序
		p1 = l;
		p2 = m + 1;
		int i = l;
		while (p1 <= m && p2 <= r) {
			clone(tmp[i++], arr[p1][2] <= arr[p2][2] ? arr[p1++] : arr[p2++]);
		}
		while (p1 <= m) {
			clone(tmp[i++], arr[p1++]);
		}
		while (p2 <= r) {
			clone(tmp[i++], arr[p2++]);
		}
		for (i = l; i <= r; i++) {
			clone(arr[i], tmp[i]);
		}
	}

	public static void cdq(int l, int r) {
		if (l == r) {
			return;
		}
		int mid = (l + r) / 2;
		cdq(l, mid);
		cdq(mid + 1, r);
		merge(l, mid, r);
	}

	public static void main(String[] args) throws IOException {
		FastReader in = new FastReader(System.in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		m = in.nextInt();
		for (int i = 1, x, y; i <= n; i++) {
			x = in.nextInt();
			y = in.nextInt();
			addTree(x, y);
		}
		for (int i = 1, a, b, c, d; i <= m; i++) {
			a = in.nextInt();
			b = in.nextInt();
			c = in.nextInt();
			d = in.nextInt();
			addQuery(c, d, 1, i);
			addQuery(a - 1, b - 1, 1, i);
			addQuery(a - 1, d, -1, i);
			addQuery(c, b - 1, -1, i);
		}
		Arrays.sort(arr, 1, cnt + 1, (a, b) -> a[1] != b[1] ? a[1] - b[1] : a[0] - b[0]);
		cdq(1, cnt);
		for (int i = 1; i <= m; i++) {
			out.println(ans[i]);
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

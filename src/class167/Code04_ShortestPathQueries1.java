package class167;

// 异或最短路，java版
// 一共有n个节点，m条边，每条边有边权
// 接下来有q条操作，每种操作是如下三种类型中的一种
// 操作 1 x y d : 原图中加入，点x到点y，权值为d的边
// 操作 2 x y   : 原图中删除，点x到点y的边
// 操作 3 x y   : 点x到点y，所有路随便走，沿途边权都异或起来，打印能取得的异或最小值
// 保证x < y，并且任意操作后，图连通、无重边、无自环，所有操作均合法
// 1 <= n、m、q <= 2 * 10^5
// 测试链接 : https://www.luogu.com.cn/problem/CF938G
// 测试链接 : https://codeforces.com/problemset/problem/938/G
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class Code04_ShortestPathQueries1 {

	public static int MAXN = 200001;
	public static int MAXT = 5000001;
	public static int BIT = 29;
	public static int n, m, q;

	// 端点x、端点y、时间点t、边权w
	public static int[][] event = new int[MAXN << 1][4];
	public static int eventCnt;

	// 操作记录下来
	public static int[] op = new int[MAXN];
	public static int[] x = new int[MAXN];
	public static int[] y = new int[MAXN];
	public static int[] d = new int[MAXN];

	// 可撤销线性基
	public static int[] basis = new int[BIT + 1];
	public static int[] inspos = new int[BIT + 1];
	public static int basiz = 0;

	// 带权并查集 + 可撤销并查集
	public static int[] father = new int[MAXN];
	public static int[] siz = new int[MAXN];
	public static int[] eor = new int[MAXN];
	public static int[][] rollback = new int[MAXN][2];
	public static int opsize = 0;

	// 时间轴线段树上的区间任务列表
	public static int[] head = new int[MAXN << 2];
	public static int[] next = new int[MAXT];
	public static int[] tox = new int[MAXT];
	public static int[] toy = new int[MAXT];
	public static int[] tow = new int[MAXT];
	public static int cnt = 0;

	// 查询操作的答案
	public static int[] ans = new int[MAXN];

	// num插入线性基
	public static void insert(int num) {
		for (int i = BIT; i >= 0; i--) {
			if (num >> i == 1) {
				if (basis[i] == 0) {
					basis[i] = num;
					inspos[basiz++] = i;
					return;
				}
				num ^= basis[i];
			}
		}
	}

	// num结合线性基，能得到的最小异或值返回
	public static int minEor(int num) {
		for (int i = BIT; i >= 0; i--) {
			num = Math.min(num, num ^ basis[i]);
		}
		return num;
	}

	// 线性基的撤销，让空间大小回到之前的规模
	public static void cancel(int oldsiz) {
		while (basiz > oldsiz) {
			basis[inspos[--basiz]] = 0;
		}
	}

	// 可撤销并查集找集合代表点
	public static int find(int i) {
		while (i != father[i]) {
			i = father[i];
		}
		return i;
	}

	// 返回i到集合代表点的异或和
	public static int getEor(int i) {
		int ans = 0;
		while (i != father[i]) {
			ans ^= eor[i];
			i = father[i];
		}
		return ans;
	}

	// 可撤销并查集的合并，增加a和b之间，权值为w的边
	// 集合合并的过程中，还要更新eor数组
	// 更新eor的方式，参考讲解156，带权并查集
	public static boolean union(int u, int v, int w) {
		int fu = find(u);
		int fv = find(v);
		w = getEor(u) ^ getEor(v) ^ w;
		if (fu == fv) {
			insert(w);
			return false;
		}
		if (siz[fu] < siz[fv]) {
			int tmp = fu;
			fu = fv;
			fv = tmp;
		}
		father[fv] = fu;
		siz[fu] += siz[fv];
		eor[fv] = w;
		rollback[++opsize][0] = fu;
		rollback[opsize][1] = fv;
		return true;
	}

	// 并查集的撤销操作
	public static void undo() {
		int fx = rollback[opsize][0];
		int fy = rollback[opsize--][1];
		father[fy] = fy;
		eor[fy] = 0;
		siz[fx] -= siz[fy];
	}

	// 给某个线段树区间增加任务，点x到点y之间，增加权值为w的边
	public static void addEdge(int i, int x, int y, int w) {
		next[++cnt] = head[i];
		tox[cnt] = x;
		toy[cnt] = y;
		tow[cnt] = w;
		head[i] = cnt;
	}

	public static void add(int jobl, int jobr, int jobx, int joby, int jobw, int l, int r, int i) {
		if (jobl <= l && r <= jobr) {
			addEdge(i, jobx, joby, jobw);
		} else {
			int mid = (l + r) >> 1;
			if (jobl <= mid) {
				add(jobl, jobr, jobx, joby, jobw, l, mid, i << 1);
			}
			if (jobr > mid) {
				add(jobl, jobr, jobx, joby, jobw, mid + 1, r, i << 1 | 1);
			}
		}
	}

	public static void dfs(int l, int r, int i) {
		int oldsiz = basiz;
		int unionCnt = 0;
		for (int e = head[i]; e > 0; e = next[e]) {
			if (union(tox[e], toy[e], tow[e])) {
				unionCnt++;
			}
		}
		if (l == r) {
			if (op[l] == 3) {
				ans[l] = minEor(getEor(x[l]) ^ getEor(y[l]));
			}
		} else {
			int mid = (l + r) >> 1;
			dfs(l, mid, i << 1);
			dfs(mid + 1, r, i << 1 | 1);
		}
		cancel(oldsiz);
		for (int k = 1; k <= unionCnt; k++) {
			undo();
		}
	}

	public static void prepare() {
		for (int i = 1; i <= n; i++) {
			father[i] = i;
			siz[i] = 1;
		}
		Arrays.sort(event, 1, eventCnt + 1,
				(a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] != b[1] ? a[1] - b[1] : a[2] - b[2]);
		int x, y, start, end, d;
		for (int l = 1, r = 1; l <= eventCnt; l = ++r) {
			x = event[l][0];
			y = event[l][1];
			while (r + 1 <= eventCnt && event[r + 1][0] == x && event[r + 1][1] == y) {
				r++;
			}
			for (int i = l; i <= r; i += 2) {
				start = event[i][2];
				end = i + 1 <= r ? (event[i + 1][2] - 1) : q;
				d = event[i][3];
				add(start, end, x, y, d, 0, q, 1);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		FastReader in = new FastReader();
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		m = in.nextInt();
		for (int i = 1; i <= m; i++) {
			event[i][0] = in.nextInt();
			event[i][1] = in.nextInt();
			event[i][2] = 0;
			event[i][3] = in.nextInt();
		}
		eventCnt = m;
		q = in.nextInt();
		for (int i = 1; i <= q; i++) {
			op[i] = in.nextInt();
			x[i] = in.nextInt();
			y[i] = in.nextInt();
			if (op[i] == 1) {
				d[i] = in.nextInt();
			}
			if (op[i] != 3) {
				event[++eventCnt][0] = x[i];
				event[eventCnt][1] = y[i];
				event[eventCnt][2] = i;
				event[eventCnt][3] = d[i];
			}
		}
		prepare();
		dfs(0, q, 1);
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
		final private int BUFFER_SIZE = 1 << 16;
		private final InputStream in;
		private final byte[] buffer;
		private int ptr, len;

		public FastReader() {
			in = System.in;
			buffer = new byte[BUFFER_SIZE];
			ptr = len = 0;
		}

		private boolean hasNextByte() throws IOException {
			if (ptr < len)
				return true;
			ptr = 0;
			len = in.read(buffer);
			return len > 0;
		}

		private byte readByte() throws IOException {
			if (!hasNextByte())
				return -1;
			return buffer[ptr++];
		}

		public char nextChar() throws IOException {
			byte c;
			do {
				c = readByte();
				if (c == -1)
					return 0;
			} while (c <= ' ');
			char ans = 0;
			while (c > ' ') {
				ans = (char) c;
				c = readByte();
			}
			return ans;
		}

		public int nextInt() throws IOException {
			int num = 0;
			byte b = readByte();
			while (isWhitespace(b))
				b = readByte();
			boolean minus = false;
			if (b == '-') {
				minus = true;
				b = readByte();
			}
			while (!isWhitespace(b) && b != -1) {
				num = num * 10 + (b - '0');
				b = readByte();
			}
			return minus ? -num : num;
		}

		private boolean isWhitespace(byte b) {
			return b == ' ' || b == '\n' || b == '\r' || b == '\t';
		}
	}

}

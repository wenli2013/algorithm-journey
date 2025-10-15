package class177;

// 树上莫队入门题，java版
// 一共有n个节点，每个节点给定颜色值，给定n-1条边，所有节点连成一棵树
// 一共有m条查询，格式 u v : 打印点u到点v的简单路径上，有几种不同的颜色
// 1 <= n <= 4 * 10^4
// 1 <= m <= 10^5
// 1 <= 颜色值 <= 2 * 10^9
// 测试链接 : https://www.luogu.com.cn/problem/SP10707
// 测试链接 : https://www.spoj.com/problems/COT2/
// 提交以下的code，提交时请把类名改成"Main"
// java实现的逻辑一定是正确的，但是本题卡常，无法通过所有测试用例
// 想通过用C++实现，本节课Code07_MoOnTree2文件就是C++的实现
// 两个版本的逻辑完全一样，C++版本可以通过所有测试

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

public class Code07_MoOnTree1 {

	public static int MAXN = 40001;
	public static int MAXM = 100001;
	public static int MAXP = 20;
	public static int n, m;
	public static int[] color = new int[MAXN];
	public static int[] sorted = new int[MAXN];
	public static int cntv;

	// 查询的参数，jobl、jobr、lca、id
	// 如果是类型1，那么lca == 0，表示空缺
	// 如果是类型2，那么lca > 0，查询结果需要补充这个单点信息
	public static int[][] query = new int[MAXM][4];

	// 链式前向星
	public static int[] head = new int[MAXN];
	public static int[] to = new int[MAXN << 1];
	public static int[] next = new int[MAXN << 1];
	public static int cntg;

	// dep是深度
	// seg是括号序
	// st是节点开始序
	// ed是节点结束序
	// stjump是倍增表
	// cntd是括号序列的长度
	public static int[] dep = new int[MAXN];
	public static int[] seg = new int[MAXN << 1];
	public static int[] st = new int[MAXN];
	public static int[] ed = new int[MAXN];
	public static int[][] stjump = new int[MAXN][MAXP];
	public static int cntd;

	// 分块
	public static int[] bi = new int[MAXN << 1];

	// 窗口信息
	public static boolean[] vis = new boolean[MAXN];
	public static int[] cnt = new int[MAXN];
	public static int kind = 0;

	public static int[] ans = new int[MAXM];

	public static void addEdge(int u, int v) {
		next[++cntg] = head[u];
		to[cntg] = v;
		head[u] = cntg;
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

	public static void dfs(int u, int fa) {
		dep[u] = dep[fa] + 1;
		seg[++cntd] = u;
		st[u] = cntd;
		stjump[u][0] = fa;
		for (int p = 1; p < MAXP; p++) {
			stjump[u][p] = stjump[stjump[u][p - 1]][p - 1];
		}
		for (int e = head[u], v; e > 0; e = next[e]) {
			v = to[e];
			if (v != fa) {
				dfs(v, u);
			}
		}
		seg[++cntd] = u;
		ed[u] = cntd;
	}

	public static int lca(int a, int b) {
		if (dep[a] < dep[b]) {
			int tmp = a;
			a = b;
			b = tmp;
		}
		for (int p = MAXP - 1; p >= 0; p--) {
			if (dep[stjump[a][p]] >= dep[b]) {
				a = stjump[a][p];
			}
		}
		if (a == b) {
			return a;
		}
		for (int p = MAXP - 1; p >= 0; p--) {
			if (stjump[a][p] != stjump[b][p]) {
				a = stjump[a][p];
				b = stjump[b][p];
			}
		}
		return stjump[a][0];
	}

	// 普通莫队经典排序
	public static class QueryCmp implements Comparator<int[]> {

		@Override
		public int compare(int[] a, int[] b) {
			if (bi[a[0]] != bi[b[0]]) {
				return bi[a[0]] - bi[b[0]];
			}
			return a[1] - b[1];
		}

	}

	// 窗口不管是加入还是删除node
	// 只要遇到node就翻转信息即可
	public static void invert(int node) {
		int val = color[node];
		if (vis[node]) {
			if (--cnt[val] == 0) {
				kind--;
			}
		} else {
			if (++cnt[val] == 1) {
				kind++;
			}
		}
		vis[node] = !vis[node];
	}

	public static void compute() {
		int winl = 1, winr = 0;
		for (int i = 1; i <= m; i++) {
			int jobl = query[i][0];
			int jobr = query[i][1];
			int lca = query[i][2];
			int id = query[i][3];
			while (winl > jobl) {
				invert(seg[--winl]);
			}
			while (winr < jobr) {
				invert(seg[++winr]);
			}
			while (winl < jobl) {
				invert(seg[winl++]);
			}
			while (winr > jobr) {
				invert(seg[winr--]);
			}
			if (lca > 0) {
				invert(lca);
			}
			ans[id] = kind;
			if (lca > 0) {
				invert(lca);
			}
		}
	}

	public static void prepare() {
		for (int i = 1; i <= n; i++) {
			sorted[i] = color[i];
		}
		Arrays.sort(sorted, 1, n + 1);
		cntv = 1;
		for (int i = 2; i <= n; i++) {
			if (sorted[cntv] != sorted[i]) {
				sorted[++cntv] = sorted[i];
			}
		}
		for (int i = 1; i <= n; i++) {
			color[i] = kth(color[i]);
		}
		// 括号序列分块
		int blen = (int) Math.sqrt(cntd);
		for (int i = 1; i <= cntd; i++) {
			bi[i] = (i - 1) / blen + 1;
		}
		Arrays.sort(query, 1, m + 1, new QueryCmp());
	}

	public static void main(String[] args) throws Exception {
		FastReader in = new FastReader(System.in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		m = in.nextInt();
		for (int i = 1; i <= n; i++) {
			color[i] = in.nextInt();
		}
		for (int i = 1, u, v; i < n; i++) {
			u = in.nextInt();
			v = in.nextInt();
			addEdge(u, v);
			addEdge(v, u);
		}
		dfs(1, 0);
		for (int i = 1, u, v, uvlca; i <= m; i++) {
			u = in.nextInt();
			v = in.nextInt();
			if (st[v] < st[u]) {
				int tmp = u;
				u = v;
				v = tmp;
			}
			uvlca = lca(u, v);
			if (u == uvlca) {
				query[i][0] = st[u];
				query[i][1] = st[v];
				query[i][2] = 0;
			} else {
				query[i][0] = ed[u];
				query[i][1] = st[v];
				query[i][2] = uvlca;
			}
			query[i][3] = i;
		}
		prepare();
		compute();
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

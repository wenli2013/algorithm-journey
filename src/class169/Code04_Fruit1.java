package class169;

// 接水果，java版
// 一共有n个点，给定n-1条无向边，所有点连成一棵树
// 一共有p个盘子，每个盘子格式 a b c : 盘子是点a到点b的路径，盘子权值为c
// 一共有q个水果，每个水果格式 u v k : 水果是点u到点v的路径，k含义如下
// 如果一个盘子路径完全在一个水果路径的内部，那么该盘子可以接住该水果
// 那么对于每个水果，可能有很多盘子都可以将其接住，打印其中第k小的权值
// 1 <= n、p、q <= 4 * 10^4
// 0 <= 盘子权值 <= 10^9
// 内存可用空间500MB
// 测试链接 : https://www.luogu.com.cn/problem/P3242
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class Code04_Fruit1 {

	public static int MAXN = 40001;
	public static int MAXH = 16;
	public static int INF = 1000000001;
	public static int n, p, q;

	// 链式前向星
	public static int[] head = new int[MAXN];
	public static int[] next = new int[MAXN << 1];
	public static int[] to = new int[MAXN << 1];
	public static int cntg = 0;

	// 树上倍增 + 每棵子树上的ldfn和rdfn
	public static int[] dep = new int[MAXN];
	public static int[] ldfn = new int[MAXN];
	public static int[] rdfn = new int[MAXN];
	public static int[][] stjump = new int[MAXN][MAXH];
	public static int cntd = 0;

	// 只有y维度的树状数组
	public static int[] tree = new int[MAXN];

	// 所有事件排完序之后，依次把下标放入eid数组
	public static int[] eid = new int[MAXN << 3];
	// 每个事件有8个属性值
	// op==1加盘子，x处加、yl、yr，盘子权值v、空缺、空缺、空缺
	// op==2删盘子，x处删、yl、yr，盘子权值v、空缺、空缺、空缺
	// op==3为水果，x、空缺、空缺、空缺、y、要求k、问题编号i
	public static int[][] event = new int[MAXN << 3][8];
	// 事件的总数
	public static int cnte = 0;

	// 整体二分
	public static int[] lset = new int[MAXN << 3];
	public static int[] rset = new int[MAXN << 3];

	// 每个水果的答案
	public static int[] ans = new int[MAXN];

	public static void addEdge(int u, int v) {
		next[++cntg] = head[u];
		to[cntg] = v;
		head[u] = cntg;
	}

	// 递归版，C++可以通过，java会爆栈
	public static void dfs1(int u, int fa) {
		dep[u] = dep[fa] + 1;
		ldfn[u] = ++cntd;
		stjump[u][0] = fa;
		for (int p = 1; p < MAXH; p++) {
			stjump[u][p] = stjump[stjump[u][p - 1]][p - 1];
		}
		for (int e = head[u]; e > 0; e = next[e]) {
			if (to[e] != fa) {
				dfs1(to[e], u);
			}
		}
		rdfn[u] = cntd;
	}

	// 不会改迭代版，去看讲解118，详解了从递归版改迭代版
	public static int[][] ufe = new int[MAXN][3];

	public static int stacksize, u, f, e;

	public static void push(int u, int f, int e) {
		ufe[stacksize][0] = u;
		ufe[stacksize][1] = f;
		ufe[stacksize][2] = e;
		stacksize++;
	}

	public static void pop() {
		--stacksize;
		u = ufe[stacksize][0];
		f = ufe[stacksize][1];
		e = ufe[stacksize][2];
	}

	// dfs1的迭代版
	public static void dfs2() {
		stacksize = 0;
		push(1, 0, -1);
		while (stacksize > 0) {
			pop();
			if (e == -1) {
				dep[u] = dep[f] + 1;
				ldfn[u] = ++cntd;
				stjump[u][0] = f;
				for (int p = 1; p < MAXH; p++) {
					stjump[u][p] = stjump[stjump[u][p - 1]][p - 1];
				}
				e = head[u];
			} else {
				e = next[e];
			}
			if (e != 0) {
				push(u, f, e);
				if (to[e] != f) {
					push(to[e], u, -1);
				}
			} else {
				rdfn[u] = cntd;
			}
		}
	}

	public static int lca(int a, int b) {
		if (dep[a] < dep[b]) {
			int tmp = a;
			a = b;
			b = tmp;
		}
		for (int p = MAXH - 1; p >= 0; p--) {
			if (dep[stjump[a][p]] >= dep[b]) {
				a = stjump[a][p];
			}
		}
		if (a == b) {
			return a;
		}
		for (int p = MAXH - 1; p >= 0; p--) {
			if (stjump[a][p] != stjump[b][p]) {
				a = stjump[a][p];
				b = stjump[b][p];
			}
		}
		return stjump[a][0];
	}

	// 已知a和b的最低公共祖先一定是a或b
	// 假设祖先为x，后代为y，返回x的哪个儿子的子树里有y
	public static int lcaSon(int a, int b) {
		if (dep[a] < dep[b]) {
			int tmp = a;
			a = b;
			b = tmp;
		}
		for (int p = MAXH - 1; p >= 0; p--) {
			if (dep[stjump[a][p]] > dep[b]) {
				a = stjump[a][p];
			}
		}
		return a;
	}

	public static int lowbit(int i) {
		return i & -i;
	}

	public static void add(int i, int v) {
		while (i <= n) {
			tree[i] += v;
			i += lowbit(i);
		}
	}

	// 树状数组中[l..r]范围上的每个数增加v
	public static void add(int l, int r, int v) {
		add(l, v);
		add(r + 1, -v);
	}

	// 树状数组中查询单点的值
	public static int query(int i) {
		int ret = 0;
		while (i > 0) {
			ret += tree[i];
			i -= lowbit(i);
		}
		return ret;
	}

	public static void addPlate(int x, int yl, int yr, int v) {
		event[++cnte][0] = 1;
		event[cnte][1] = x;
		event[cnte][2] = yl;
		event[cnte][3] = yr;
		event[cnte][4] = v;
	}

	public static void delPlate(int x, int yl, int yr, int v) {
		event[++cnte][0] = 2;
		event[cnte][1] = x;
		event[cnte][2] = yl;
		event[cnte][3] = yr;
		event[cnte][4] = v;
	}

	public static void addFruit(int x, int y, int k, int i) {
		event[++cnte][0] = 3;
		event[cnte][1] = x;
		// 2、3、4位空缺
		event[cnte][5] = y;
		event[cnte][6] = k;
		event[cnte][7] = i;
	}

	public static void compute(int el, int er, int vl, int vr) {
		if (el > er) {
			return;
		}
		if (vl == vr) {
			for (int i = el; i <= er; i++) {
				int id = eid[i];
				if (event[id][0] == 3) {
					ans[event[id][7]] = vl;
				}
			}
		} else {
			int mid = (vl + vr) >> 1;
			int lsiz = 0, rsiz = 0;
			for (int i = el; i <= er; i++) {
				int id = eid[i];
				if (event[id][0] == 1) {
					if (event[id][4] <= mid) {
						add(event[id][2], event[id][3], 1);
						lset[++lsiz] = id;
					} else {
						rset[++rsiz] = id;
					}
				} else if (event[id][0] == 2) {
					if (event[id][4] <= mid) {
						add(event[id][2], event[id][3], -1);
						lset[++lsiz] = id;
					} else {
						rset[++rsiz] = id;
					}
				} else {
					int satisfy = query(event[id][5]);
					if (satisfy >= event[id][6]) {
						lset[++lsiz] = id;
					} else {
						event[id][6] -= satisfy;
						rset[++rsiz] = id;
					}
				}
			}
			// 这里为什么不用做撤销？
			// 因为任何一个盘子，一定有两条扫描线
			// 一条扫描线会增加yl..yr的计数
			// 一条扫描线会减少yl..yr的计数
			// 同一个盘子的两条扫描线，一定会在一起，是不可能分开的
			// 所以此时树状数组就是清空的，不需要再做撤销操作
			for (int i = 1; i <= lsiz; i++) {
				eid[el + i - 1] = lset[i];
			}
			for (int i = 1; i <= rsiz; i++) {
				eid[el + lsiz + i - 1] = rset[i];
			}
			compute(el, el + lsiz - 1, vl, mid);
			compute(el + lsiz, er, mid + 1, vr);
		}
	}

	public static void main(String[] args) throws Exception {
		FastReader in = new FastReader(System.in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		n = in.nextInt();
		p = in.nextInt();
		q = in.nextInt();
		for (int i = 1, u, v; i < n; i++) {
			u = in.nextInt();
			v = in.nextInt();
			addEdge(u, v);
			addEdge(v, u);
		}
		dfs2();
		for (int i = 1; i <= p; i++) {
			int a = in.nextInt();
			int b = in.nextInt();
			int c = in.nextInt();
			if (ldfn[a] > ldfn[b]) {
				int tmp = a;
				a = b;
				b = tmp;
			}
			int ablca = lca(a, b);
			// 类型1，a和b的lca是a或b，2个区域，4个事件产生
			// 类型2，a和b的lca不是a或b，1个区域，2个事件产生
			if (ablca == a || ablca == b) {
				int son = lcaSon(a, b);
				// (1 ~ dfn[son]-1) (b子树上的dfn范围)
				addPlate(1, ldfn[b], rdfn[b], c);
				delPlate(ldfn[son], ldfn[b], rdfn[b], c);
				// (b子树上的dfn范围) (son子树上最大的dfn序号+1 ~ n)
				addPlate(ldfn[b], rdfn[son] + 1, n, c);
				delPlate(rdfn[b] + 1, rdfn[son] + 1, n, c);
			} else {
				// (a子树上的dfn范围) (b子树上的dfn范围)
				addPlate(ldfn[a], ldfn[b], rdfn[b], c);
				delPlate(rdfn[a] + 1, ldfn[b], rdfn[b], c);
			}
		}
		for (int i = 1; i <= q; i++) {
			int u = in.nextInt();
			int v = in.nextInt();
			int k = in.nextInt();
			addFruit(Math.min(ldfn[u], ldfn[v]), Math.max(ldfn[u], ldfn[v]), k, i);
		}
		// 根据x排序，如果x一样，加盘子排最前、删盘子其次、水果最后
		Arrays.sort(event, 1, cnte + 1, (a, b) -> a[1] != b[1] ? a[1] - b[1] : a[0] - b[0]);
		for (int i = 1; i <= cnte; i++) {
			eid[i] = i;
		}
		compute(1, cnte, 0, INF);
		for (int i = 1; i <= q; i++) {
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

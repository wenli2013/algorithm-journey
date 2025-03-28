package class164;

// 归程，java实现递归版
// 测试链接 : https://www.luogu.com.cn/problem/P4768
// 提交以下的code，提交时请把类名改成"Main"
// 因为树的深度太大，递归函数爆栈了，所以无法全部通过所有测试用例
// find方法、dfs方法都需要改成迭代版，就是本节课Code02_ReturnJourney2文件

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Code02_ReturnJourney1 {

	public static int MAXN = 200001;
	public static int MAXK = 400001;
	public static int MAXM = 400001;
	public static int MAXH = 20;
	public static int INF = 2000000001;
	public static int t, n, m, q, k, s;
	public static int[][] arr = new int[MAXM][4];

	public static int[] headg = new int[MAXN];
	public static int[] nextg = new int[MAXM << 1];
	public static int[] tog = new int[MAXM << 1];
	public static int[] weightg = new int[MAXM << 1];
	public static int cntg;

	public static int[] dist = new int[MAXN];
	public static boolean[] visit = new boolean[MAXN];
	public static PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[1] - b[1]);

	public static int[] headk = new int[MAXK];
	public static int[] nextk = new int[MAXK];
	public static int[] tok = new int[MAXK];
	public static int cntk;

	public static int[] father = new int[MAXK];
	public static int[] nodeKey = new int[MAXK];
	public static int cntu;

	public static int[] dep = new int[MAXK];
	public static int[][] stjump = new int[MAXK][MAXH];
	public static int[] mindist = new int[MAXK];

	public static void prepare() {
		cntg = cntk = 0;
		Arrays.fill(headg, 1, n + 1, 0);
		Arrays.fill(headk, 1, n * 2, 0);
	}

	public static void addEdgeG(int u, int v, int w) {
		nextg[++cntg] = headg[u];
		tog[cntg] = v;
		weightg[cntg] = w;
		headg[u] = cntg;
	}

	public static void dijkstra() {
		for (int i = 1; i <= m; i++) {
			addEdgeG(arr[i][0], arr[i][1], arr[i][2]);
			addEdgeG(arr[i][1], arr[i][0], arr[i][2]);
		}
		Arrays.fill(dist, 1, n + 1, INF);
		Arrays.fill(visit, 1, n + 1, false);
		dist[1] = 0;
		heap.add(new int[] { 1, 0 });
		int[] cur;
		int x, v;
		while (!heap.isEmpty()) {
			cur = heap.poll();
			x = cur[0];
			v = cur[1];
			if (!visit[x]) {
				visit[x] = true;
				for (int e = headg[x], y, w; e > 0; e = nextg[e]) {
					y = tog[e];
					w = weightg[e];
					if (!visit[y] && dist[y] > v + w) {
						dist[y] = v + w;
						heap.add(new int[] { y, dist[y] });
					}
				}
			}
		}
	}

	public static void addEdgeK(int u, int v) {
		nextk[++cntk] = headk[u];
		tok[cntk] = v;
		headk[u] = cntk;
	}

	public static int find(int i) {
		if (i != father[i]) {
			father[i] = find(father[i]);
		}
		return father[i];
	}

	public static void kruskalRebuild() {
		for (int i = 1; i <= n; i++) {
			father[i] = i;
		}
		Arrays.sort(arr, 1, m + 1, (a, b) -> b[3] - a[3]);
		cntu = n;
		for (int i = 1, fx, fy; i <= m; i++) {
			fx = find(arr[i][0]);
			fy = find(arr[i][1]);
			if (fx != fy) {
				father[fx] = father[fy] = ++cntu;
				father[cntu] = cntu;
				nodeKey[cntu] = arr[i][3];
				addEdgeK(cntu, fx);
				addEdgeK(cntu, fy);
			}
		}
	}

	public static void dfs(int u, int fa) {
		dep[u] = dep[fa] + 1;
		stjump[u][0] = fa;
		for (int p = 1; p < MAXH; p++) {
			stjump[u][p] = stjump[stjump[u][p - 1]][p - 1];
		}
		for (int e = headk[u]; e > 0; e = nextk[e]) {
			dfs(tok[e], u);
		}
		if (u <= n) {
			mindist[u] = dist[u];
		} else {
			mindist[u] = INF;
		}
		for (int e = headk[u]; e > 0; e = nextk[e]) {
			mindist[u] = Math.min(mindist[u], mindist[tok[e]]);
		}
	}

	public static int query(int node, int line) {
		for (int p = MAXH - 1; p >= 0; p--) {
			if (stjump[node][p] > 0 && nodeKey[stjump[node][p]] > line) {
				node = stjump[node][p];
			}
		}
		return mindist[node];
	}

	public static void main(String[] args) {
		FastIO io = new FastIO(System.in, System.out);
		t = io.nextInt();
		for (int test = 1; test <= t; test++) {
			n = io.nextInt();
			m = io.nextInt();
			prepare();
			for (int i = 1; i <= m; i++) {
				arr[i][0] = io.nextInt();
				arr[i][1] = io.nextInt();
				arr[i][2] = io.nextInt();
				arr[i][3] = io.nextInt();
			}
			dijkstra();
			kruskalRebuild();
			dfs(cntu, 0);
			q = io.nextInt();
			k = io.nextInt();
			s = io.nextInt();
			for (int i = 1, node, line, lastAns = 0; i <= q; i++) {
				node = (io.nextInt() + k * lastAns - 1) % n + 1;
				line = (io.nextInt() + k * lastAns) % (s + 1);
				lastAns = query(node, line);
				io.writelnInt(lastAns);
			}
		}
		io.flush();
	}

	// 读写工具类
	static class FastIO {
		private final InputStream is;
		private final OutputStream os;
		private final byte[] inbuf = new byte[1 << 16];
		private int lenbuf = 0;
		private int ptrbuf = 0;
		private final StringBuilder outBuf = new StringBuilder();

		public FastIO(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
		}

		private int readByte() {
			if (ptrbuf >= lenbuf) {
				ptrbuf = 0;
				try {
					lenbuf = is.read(inbuf);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (lenbuf == -1) {
					return -1;
				}
			}
			return inbuf[ptrbuf++] & 0xff;
		}

		private int skip() {
			int b;
			while ((b = readByte()) != -1) {
				if (b > ' ') {
					return b;
				}
			}
			return -1;
		}

		public int nextInt() {
			int b = skip();
			if (b == -1) {
				throw new RuntimeException("No more integers (EOF)");
			}
			boolean negative = false;
			if (b == '-') {
				negative = true;
				b = readByte();
			}
			int val = 0;
			while (b >= '0' && b <= '9') {
				val = val * 10 + (b - '0');
				b = readByte();
			}
			return negative ? -val : val;
		}

		public void write(String s) {
			outBuf.append(s);
		}

		public void writeInt(int x) {
			outBuf.append(x);
		}

		public void writelnInt(int x) {
			outBuf.append(x).append('\n');
		}

		public void flush() {
			try {
				os.write(outBuf.toString().getBytes());
				os.flush();
				outBuf.setLength(0);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}

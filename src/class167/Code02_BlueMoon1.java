package class167;

// 贪玩蓝月，java版
// 每件装备都有特征值w和战斗力v，放装备的背包是一个双端队列，只有背包中的装备是可选的
// 给定数值p，接下来有m条操作，每种操作是如下五种类型中的一种
// 操作 IF x y : 背包前端加入一件特征值x、战斗力y的装备
// 操作 IG x y : 背包后端加入一件特征值x、战斗力y的装备
// 操作 DF     : 删除背包前端的装备
// 操作 DG     : 删除背包后端的装备
// 操作 QU x y : 选择装备的特征值累加和 % p，必须在[x, y]范围，打印最大战斗力，无方案打印-1
// 1 <= m <= 5 * 10^4    1 <= p <= 500
// 0 <= 每件装备特征值、每件装备战斗力 <= 10^9
// 测试链接 : https://loj.ac/p/6515
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;

public class Code02_BlueMoon1 {

	public static int MAXM = 50001;
	public static int MAXP = 501;
	public static int MAXT = 1000001;
	public static int DEEP = 20;
	public static int m, p;

	public static int[] op = new int[MAXM];
	public static int[] x = new int[MAXM];
	public static int[] y = new int[MAXM];

	// 背包<装备特征值%p、装备战斗力、装备出现时间点>
	public static Deque<int[]> knapsack = new ArrayDeque<>();

	// 时间轴线段树的区间上挂上生效的装备，(特征值 % p)记为w，战斗力记为v
	public static int[] head = new int[MAXM << 2];
	public static int[] next = new int[MAXT];
	public static int[] tow = new int[MAXT];
	public static int[] tov = new int[MAXT];
	public static int cnt = 0;

	// 动态规划表不考虑当前装备的状态，上一行的状态
	public static long[] pre = new long[MAXP];
	// 动态规划表考虑当前装备的状态，本行的状态，需要更新
	public static long[] dp = new long[MAXP];
	// 动态规划表的备份
	public static long[][] backup = new long[DEEP][MAXP];

	// 答案
	public static long[] ans = new long[MAXM];

	public static void clone(long[] a, long[] b) {
		for (int i = 0; i < p; i++) {
			a[i] = b[i];
		}
	}

	public static void addEdge(int i, int w, int v) {
		next[++cnt] = head[i];
		tow[cnt] = w;
		tov[cnt] = v;
		head[i] = cnt;
	}

	public static void add(int jobl, int jobr, int jobw, int jobv, int l, int r, int i) {
		if (jobl <= l && r <= jobr) {
			addEdge(i, jobw, jobv);
		} else {
			int mid = (l + r) >> 1;
			if (jobl <= mid) {
				add(jobl, jobr, jobw, jobv, l, mid, i << 1);
			}
			if (jobr > mid) {
				add(jobl, jobr, jobw, jobv, mid + 1, r, i << 1 | 1);
			}
		}
	}

	public static void dfs(int l, int r, int i, int dep) {
		clone(backup[dep], dp);
		for (int e = head[i], w, v; e > 0; e = next[e]) {
			w = tow[e];
			v = tov[e];
			clone(pre, dp);
			for (int j = 0; j < p; j++) {
				if (pre[j] != -1) {
					dp[(j + w) % p] = Math.max(dp[(j + w) % p], pre[j] + v);
				}
			}
		}
		if (l == r) {
			if (op[l] == 5) {
				long ret = -1;
				for (int j = x[l]; j <= y[l]; j++) {
					ret = Math.max(ret, dp[j]);
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
		int[] equip;
		for (int i = 1; i <= m; i++) {
			if (op[i] == 1) {
				knapsack.addFirst(new int[] { x[i] % p, y[i], i });
			} else if (op[i] == 2) {
				knapsack.addLast(new int[] { x[i] % p, y[i], i });
			} else if (op[i] == 3) {
				equip = knapsack.pollFirst();
				add(equip[2], i - 1, equip[0], equip[1], 1, m, 1);
			} else if (op[i] == 4) {
				equip = knapsack.pollLast();
				add(equip[2], i - 1, equip[0], equip[1], 1, m, 1);
			}
		}
		while (!knapsack.isEmpty()) {
			equip = knapsack.pollFirst();
			add(equip[2], m, equip[0], equip[1], 1, m, 1);
		}
		for (int i = 0; i < p; i++) {
			dp[i] = -1;
		}
		dp[0] = 0;
	}

	public static void main(String[] args) throws IOException {
		FastReader in = new FastReader();
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		in.nextInt();
		m = in.nextInt();
		p = in.nextInt();
		String t;
		for (int i = 1; i <= m; i++) {
			t = in.nextString();
			if (t.equals("IF")) {
				op[i] = 1;
				x[i] = in.nextInt();
				y[i] = in.nextInt();
			} else if (t.equals("IG")) {
				op[i] = 2;
				x[i] = in.nextInt();
				y[i] = in.nextInt();
			} else if (t.equals("DF")) {
				op[i] = 3;
			} else if (t.equals("DG")) {
				op[i] = 4;
			} else {
				op[i] = 5;
				x[i] = in.nextInt();
				y[i] = in.nextInt();
			}
		}
		prepare();
		dfs(1, m, 1, 1);
		for (int i = 1; i <= m; i++) {
			if (op[i] == 5) {
				out.println(ans[i]);
			}
		}
		out.flush();
		out.close();
	}

	// 读写工具类
	static class FastReader {
		private static final int BUFFER_SIZE = 1 << 16;
		private final InputStream in;
		private final byte[] buffer;
		private int ptr, len;

		public FastReader() {
			in = System.in;
			buffer = new byte[BUFFER_SIZE];
			ptr = len = 0;
		}

		private boolean hasNextByte() throws IOException {
			if (ptr < len) {
				return true;
			}
			ptr = 0;
			len = in.read(buffer);
			return len > 0;
		}

		private byte readByte() throws IOException {
			if (!hasNextByte()) {
				return -1;
			}
			return buffer[ptr++];
		}

		public int nextInt() throws IOException {
			int num = 0;
			byte b = readByte();
			while (isWhitespace(b)) {
				b = readByte();
			}
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

		public String nextString() throws IOException {
			byte b = readByte();
			while (isWhitespace(b)) {
				b = readByte();
			}
			StringBuilder sb = new StringBuilder(1000);
			while (!isWhitespace(b) && b != -1) {
				sb.append((char) b);
				b = readByte();
			}
			return sb.toString();
		}

		private boolean isWhitespace(byte b) {
			return b == ' ' || b == '\n' || b == '\r' || b == '\t';
		}
	}

}
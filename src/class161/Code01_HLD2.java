package class161;

// 重链剖分模版题，C++版
// 一共有n个节点，给定n-1条边，节点连成一棵树
// 给定每个节点的初始权值，给定树的头节点编号root
// 一共有m条操作，每种操作是如下4种类型中的一种
// 操作 1 x y v : x到y的路径上，每个节点值增加v
// 操作 2 x y   : x到y的路径上，打印所有节点值的累加和
// 操作 3 x v   : x为头的子树上，每个节点值增加v
// 操作 4 x     : x为头的子树上，打印所有节点值的累加和
// 1 <= n、m <= 10^5
// 1 <= MOD <= 2^30
// 输入的值都为int类型
// 查询操作时，打印(查询结果 % MOD)，题目会给定MOD值
// 测试链接 : https://www.luogu.com.cn/problem/P3384
// 如下实现是C++的版本，C++版本和java版本逻辑完全一样
// 提交如下代码，可以通过所有测试用例

//#include <bits/stdc++.h>
//
//using namespace std;
//
//const int MAXN = 100001;
//int n, m, root, MOD;
//int arr[MAXN];
//
//int head[MAXN];
//int nxt[MAXN << 1];
//int to[MAXN << 1];
//int cntg = 0;
//
//int fa[MAXN];
//int dep[MAXN];
//int siz[MAXN];
//int son[MAXN];
//int top[MAXN];
//int dfn[MAXN];
//int seg[MAXN];
//int cntd = 0;
//
//long long sum[MAXN << 2];
//long long addTag[MAXN << 2];
//
//void addEdge(int u, int v) {
//    nxt[++cntg] = head[u];
//    to[cntg] = v;
//    head[u] = cntg;
//}
//
//void dfs1(int u, int f) {
//    fa[u] = f;
//    dep[u] = dep[f] + 1;
//    siz[u] = 1;
//    for (int e = head[u], v; e > 0; e = nxt[e]) {
//        v = to[e];
//        if (v != f) {
//            dfs1(v, u);
//        }
//    }
//    for (int e = head[u], v; e > 0; e = nxt[e]) {
//        v = to[e];
//        if (v != f) {
//            siz[u] += siz[v];
//            if (son[u] == 0 || siz[son[u]] < siz[v]) {
//                son[u] = v;
//            }
//        }
//    }
//}
//
//void dfs2(int u, int t) {
//    top[u] = t;
//    dfn[u] = ++cntd;
//    seg[cntd] = u;
//    if (son[u] == 0) {
//        return;
//    }
//    dfs2(son[u], t);
//    for (int e = head[u], v; e > 0; e = nxt[e]) {
//        v = to[e];
//        if (v != fa[u] && v != son[u]) {
//            dfs2(v, v);
//        }
//    }
//}
//
//void up(int i) {
//    sum[i] = (sum[i << 1] + sum[i << 1 | 1]) % MOD;
//}
//
//void lazy(int i, long long v, int n) {
//    sum[i] = (sum[i] + v * n) % MOD;
//    addTag[i] = (addTag[i] + v) % MOD;
//}
//
//void down(int i, int ln, int rn) {
//    if (addTag[i] != 0) {
//        lazy(i << 1, addTag[i], ln);
//        lazy(i << 1 | 1, addTag[i], rn);
//        addTag[i] = 0;
//    }
//}
//
//void build(int l, int r, int i) {
//    if (l == r) {
//        sum[i] = arr[seg[l]] % MOD;
//    } else {
//        int mid = (l + r) / 2;
//        build(l, mid, i << 1);
//        build(mid + 1, r, i << 1 | 1);
//        up(i);
//    }
//}
//
//void add(int jobl, int jobr, int jobv, int l, int r, int i) {
//    if (jobl <= l && r <= jobr) {
//        lazy(i, jobv, r - l + 1);
//    } else {
//        int mid = (l + r) / 2;
//        down(i, mid - l + 1, r - mid);
//        if (jobl <= mid) {
//            add(jobl, jobr, jobv, l, mid, i << 1);
//        }
//        if (jobr > mid) {
//            add(jobl, jobr, jobv, mid + 1, r, i << 1 | 1);
//        }
//        up(i);
//    }
//}
//
//long long query(int jobl, int jobr, int l, int r, int i) {
//    if (jobl <= l && r <= jobr) {
//        return sum[i];
//    }
//    int mid = (l + r) / 2;
//    down(i, mid - l + 1, r - mid);
//    long long ans = 0;
//    if (jobl <= mid) {
//        ans = (ans + query(jobl, jobr, l, mid, i << 1)) % MOD;
//    }
//    if (jobr > mid) {
//        ans = (ans + query(jobl, jobr, mid + 1, r, i << 1 | 1)) % MOD;
//    }
//    return ans;
//}
//
//void pathAdd(int x, int y, int v) {
//    while (top[x] != top[y]) {
//        if (dep[top[x]] <= dep[top[y]]) {
//            add(dfn[top[y]], dfn[y], v, 1, n, 1);
//            y = fa[top[y]];
//        } else {
//            add(dfn[top[x]], dfn[x], v, 1, n, 1);
//            x = fa[top[x]];
//        }
//    }
//    add(min(dfn[x], dfn[y]), max(dfn[x], dfn[y]), v, 1, n, 1);
//}
//
//void subtreeAdd(int x, int v) {
//    add(dfn[x], dfn[x] + siz[x] - 1, v, 1, n, 1);
//}
//
//long long pathSum(int x, int y) {
//    long long ans = 0;
//    while (top[x] != top[y]) {
//        if (dep[top[x]] <= dep[top[y]]) {
//            ans = (ans + query(dfn[top[y]], dfn[y], 1, n, 1)) % MOD;
//            y = fa[top[y]];
//        } else {
//            ans = (ans + query(dfn[top[x]], dfn[x], 1, n, 1)) % MOD;
//            x = fa[top[x]];
//        }
//    }
//    ans = (ans + query(min(dfn[x], dfn[y]), max(dfn[x], dfn[y]), 1, n, 1)) % MOD;
//    return ans;
//}
//
//long long subtreeSum(int x) {
//    return query(dfn[x], dfn[x] + siz[x] - 1, 1, n, 1);
//}
//
//int main() {
//    ios::sync_with_stdio(false);
//    cin.tie(nullptr);
//    cin >> n >> m >> root >> MOD;
//    for (int i = 1; i <= n; i++) {
//        cin >> arr[i];
//    }
//    for (int i = 1, u, v; i < n; i++) {
//        cin >> u >> v;
//        addEdge(u, v);
//        addEdge(v, u);
//    }
//    dfs1(root, 0);
//    dfs2(root, root);
//    build(1, n, 1);
//    for (int i = 1, op, x, y, v; i <= m; i++) {
//        cin >> op;
//        if (op == 1) {
//            cin >> x >> y >> v;
//            pathAdd(x, y, v);
//        } else if (op == 2) {
//            cin >> x >> y;
//            cout << pathSum(x, y) << "\n";
//        } else if (op == 3) {
//            cin >> x >> v;
//            subtreeAdd(x, v);
//        } else {
//            cin >> x;
//            cout << subtreeSum(x) << "\n";
//        }
//    }
//    return 0;
//}
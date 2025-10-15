package class170;

// 三维偏序，C++版
// 一共有n个对象，属性值范围[1, k]，每个对象有a属性、b属性、c属性
// f(i)表示，aj <= ai 且 bj <= bi 且 cj <= ci 且 j != i 的j的数量
// ans(d)表示，f(i) == d 的i的数量
// 打印所有的ans[d]，d的范围[0, n)
// 1 <= n <= 10^5
// 1 <= k <= 2 * 10^5
// 测试链接 : https://www.luogu.com.cn/problem/P3810
// 如下实现是C++的版本，C++版本和java版本逻辑完全一样
// 提交如下代码，可以通过所有测试用例

//#include <bits/stdc++.h>
//
//using namespace std;
//
//struct Node {
//    int i, a, b, c;
//};
//
//bool CmpAbc(Node x, Node y) {
//    if (x.a != y.a) {
//        return x.a < y.a;
//    }
//    if (x.b != y.b) {
//        return x.b < y.b;
//    }
//    return x.c < y.c;
//}
//
//bool CmpB(Node x, Node y) {
//    return x.b < y.b;
//}
//
//const int MAXN = 100001;
//const int MAXK = 200001;
//int n, k;
//
//Node arr[MAXN];
//int tree[MAXK];
//int f[MAXN];
//int ans[MAXN];
//
//int lowbit(int i) {
//    return i & -i;
//}
//
//void add(int i, int v) {
//    while (i <= k) {
//        tree[i] += v;
//        i += lowbit(i);
//    }
//}
//
//int query(int i) {
//    int ret = 0;
//    while (i > 0) {
//        ret += tree[i];
//        i -= lowbit(i);
//    }
//    return ret;
//}
//
//void merge(int l, int m, int r) {
//    int p1, p2;
//    for (p1 = l - 1, p2 = m + 1; p2 <= r; p2++) {
//        while (p1 + 1 <= m && arr[p1 + 1].b <= arr[p2].b) {
//            p1++;
//            add(arr[p1].c, 1);
//        }
//        f[arr[p2].i] += query(arr[p2].c);
//    }
//    for (int i = l; i <= p1; i++) {
//        add(arr[i].c, -1);
//    }
//    sort(arr + l, arr + r + 1, CmpB);
//}
//
//void cdq(int l, int r) {
//    if (l == r) {
//        return;
//    }
//    int mid = (l + r) / 2;
//    cdq(l, mid);
//    cdq(mid + 1, r);
//    merge(l, mid, r);
//}
//
//void prepare() {
//    sort(arr + 1, arr + n + 1, CmpAbc);
//    for (int l = 1, r = 1; l <= n; l = ++r) {
//        while (r + 1 <= n && arr[l].a == arr[r + 1].a && arr[l].b == arr[r + 1].b && arr[l].c == arr[r + 1].c) {
//            r++;
//        }
//        for (int i = l; i <= r; i++) {
//            f[arr[i].i] = r - i;
//        }
//    }
//}
//
//int main() {
//    ios::sync_with_stdio(false);
//    cin.tie(nullptr);
//    cin >> n >> k;
//    for (int i = 1; i <= n; i++) {
//        arr[i].i = i;
//        cin >> arr[i].a >> arr[i].b >> arr[i].c;
//    }
//    prepare();
//    cdq(1, n);
//    for (int i = 1; i <= n; i++) {
//        ans[f[i]]++;
//    }
//    for (int i = 0; i < n; i++) {
//        cout << ans[i] << '\n';
//    }
//    return 0;
//}
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Arrays;
import java.util.ArrayList;

class KeyNodeArr{ // splitNode �Լ����� key�� TreeNode�� �ѹ��� return�ϱ����� ����� class
	int key = 0;
	TreeNode node = null;
	
	KeyNodeArr(int key, TreeNode t){
		this.key = key;
		this.node = t;
	}
}

class TreeNode{
	int n; // n: �ش� ��尡 ���� �ִ� Ű�� ����
	int[] K = null; // �ش� ��尡 ���� �ִ� Ű
	TreeNode[] P = null; // �ش� ����� �ڽĳ��
	
	TreeNode(int m){
		this.n = 0;
		this.K = new int[m]; // insertKey�� ���� �ʿ��� ũ�⺸�� 1 ũ�� ����
		this.P = new TreeNode[m+1]; // insertKey�� ���� �ʿ��� ũ�⺸�� 1 ũ�� ���� 
	}
	
	TreeNode(int n, int[] k, TreeNode[] p) {
		this.n = n;
		this.K = k;
		this.P = p;
	}
}

public class BT {
	TreeNode root = null; // root node
	Deque<TreeNode> stack = new ArrayDeque<>(); // stack
	
	// node�� ã�� �߰� ���θ� return�ϰ� ��θ� stack�� ����
	boolean searchPath(TreeNode T, int m, int key) { // m: Ʈ���� ����, key: ã�� key	
		TreeNode x = T;
		int i = 0;
		do {
			i = 0;
			while(i < x.n && key > x.K[i]) i += 1;
			
			// ������ Ű �߰����� ��� -> �̹� Ʈ���� �����ϹǷ� ���� �Ұ�
			stack.push(x); // ��� update
			if(i < x.n && key == x.K[i]) return true; // ����
			
			// ������ Ű�� ���� �߰����� ������ ���
		} while((x = x.P[i]) != null);
		return false;
	}
	
	// node x�� ������ ��ġ�� ����
	void insertKey(int m, TreeNode x, TreeNode y, int newKey) { // y: �ڽ� ��忡�� split�Ǿ� ���� ������� ���
		// newKey���� ū key���� ���������� �� ĭ�� �̵�
		int i = x.n - 1;
		
		while(i >= 0 && newKey < x.K[i]) {
			x.K[i+1] = x.K[i];
			x.P[i+2] = x.P[i+1];
			i -= 1;
		}
		
		// newKey ����
		x.K[i+1] = newKey;
		x.P[i+2] = y;
		x.n += 1;
	}
	
	void copyNode(TreeNode a, TreeNode b, int m) { // copy a to b
		b.n = a.n;
		for(int i = 0; i < m; i++) {
			b.K[i] = a.K[i];
			b.P[i] = a.P[i];
		}
	}
	
	void clearNode(TreeNode t, int m) { // node�� n, K, P�� ��� �ʱ�ȭ
		t.n = 0;
		for(int i=0; i<m; i++) {
			t.K[i] = 0;
			t.P[i] = null;
		}
	}
	
	// split ����
	KeyNodeArr splitNode(int m, TreeNode x, TreeNode y, int newKey) {
		TreeNode tmpNode = new TreeNode(m+1);
		copyNode(x, tmpNode, m); // overflow�� ���� �ӽ� ���
		insertKey(m, tmpNode, y, newKey);
		
		int center = tmpNode.n/2;
		int centerKey = tmpNode.K[center]; // ���� ����
		
		// centerKey ���� ���� ��� x�� ����
		clearNode(x, m);
		int i = 0;
		
		while(tmpNode.K[i] < centerKey) {
			x.K[i] = tmpNode.K[i];
			x.P[i] = tmpNode.P[i];
			i += 1;
			x.n += 1;
		}
		x.P[i] = tmpNode.P[i];
		
		TreeNode newNode = new TreeNode(m); // centerKey ���� ���� ��� newNode�� ����
		i += 1; // centerKey �� �ǳʶ�
		while(i < tmpNode.n) {
			newNode.K[newNode.n] = tmpNode.K[i];
			newNode.P[newNode.n] = tmpNode.P[i];
			i += 1;
			newNode.n += 1;
		}
		newNode.P[newNode.n] = tmpNode.P[i];
		
		return new KeyNodeArr(centerKey, newNode);
	}
	
	// oldKey�� x���� ����
	void deleteKey(int m, TreeNode x, int oldKey) {
		// oldKey�� ��ġ i Ž��
		int i = 0;
		while(oldKey > x.K[i]) i += 1;
		
		// oldKey���� ū key���� �������� �� ĭ�� �̵�, ���� i�� oldKey ��ġ
		while(i < x.n) {
			x.K[i] = x.K[i+1];
			x.P[i+1] = x.P[i+2];
			i += 1;
		}
		x.n -= 1;
	}
	
	// x�� best ���� ��� ��ġ ��ȯ
	int bestSibling(int m, TreeNode x, TreeNode y) { // y: x�� �θ� ���
		// y���� x�� ��ġ i�� Ž��
		int i = 0;
		while(y.P[i] != x) i += 1;
		
		// �ٷ� ������ �� ���� ��, Ű�� ������ ���� ������ bestSibling���� ����
		if(i == 0) return i+1; // ���� ���� ����
		else if(i == y.n) return i-1; // ������ ���� ����
		else if(y.P[i].n >= y.P[i+1].n) return i-1;
		else return i+1;
	}
	
	// x�� best sibling ��� ���� Ű ��й� ����
	void redistributeKeys(int m, TreeNode x, TreeNode y, int bs) { // y: x�� �θ� ���, bs: best sibling index
		// y���� x�� ��ġ i�� Ž��
		int i = 0;
		while(y.P[i] != x) i += 1;
		
		TreeNode bestNode = y.P[bs];
		if(bs < i) {
			int lastKey = bestNode.K[bestNode.n-1];
			insertKey(m, x, null, y.K[i-1]);
			x.P[1] = x.P[0];
			x.P[0] = bestNode.P[bestNode.n];
			bestNode.P[bestNode.n] = null;
			deleteKey(m, bestNode, lastKey);
			y.K[i-1] = lastKey;
		} else {
			int firstKey = bestNode.K[0];
			insertKey(m, x, null, y.K[i]);
			x.P[x.n] = bestNode.P[0];
		    bestNode.P[0] = bestNode.P[1];
			deleteKey(m, bestNode, firstKey);
			y.K[i] = firstKey;
		}
	}
	
	// x�� best sibling ��� ���� �պ� ����
	void mergeNode(int m, TreeNode x, TreeNode y, int bs) {
		int i = 0; // y���� x�� ��ġ iŽ��
		while(y.P[i] != x) i += 1;
		
		TreeNode bestNode = y.P[bs];
		// ���� ���� ������ ���ո� ����� �� �ֵ��� swap
		if(bs > i) {
			int tmp = i;
			i = bs;
			bs = tmp;
			
			TreeNode tmpNode = x;
			x = bestNode;
			bestNode = tmpNode;
		}
		// ���� ���� ���� ����
		bestNode.K[bestNode.n] = y.K[i-1];
		bestNode.n += 1;
		int j = 0;
		while(j < x.n) {
			bestNode.K[bestNode.n] = x.K[j];
			bestNode.P[bestNode.n] = x.P[j];
			bestNode.n += 1;
			j += 1;
		}
		bestNode.P[bestNode.n] = x.P[x.n];
		deleteKey(m, y, y.K[i-1]);
	}
	
	// ��� ����
	void insertBT(int m, int newKey) { // m: Ʈ���� ����, newKey: ���ο� ����� Ű
		// root node ����
		if(this.root == null) {
			this.root = new TreeNode(m);
			this.root.K[0] = newKey;
			this.root.n = 1;
			return;
		}
		
		// newKey�� ������ ����� ��θ� Ž���ϸ� ���ÿ� ��� ����
		stack.clear();
		boolean found = searchPath(this.root, m, newKey);
		if(found) {
			System.out.println(String.format("i %d : The key already exists.", newKey));
			return; // �̹� newKey�� �����ϴ� ���
		}
		
		// newKey�� �������� �ʴ� ���, ���� ����
		boolean finished = false;
		
		TreeNode x = stack.pop(); // ����� top
		TreeNode y = null; // ���� ���ҵ� ��带 ���� ����
		
		do {
			if(x.n < m-1) { // overflow �߻� ����
				// overflow �߻� ����, newKey ����
				insertKey(m, x, y, newKey);
				finished = true;
			} else { // overflow �߻�
				// x�� newKey�� �������� ����, ���ҵ� ��� ��ȯ
				KeyNodeArr ans = splitNode(m, x, y, newKey);
				newKey = ans.key;
				y = ans.node;
				if(!stack.isEmpty()) x = stack.pop();
				else { // tree level 1 ����
					this.root = new TreeNode(m);
					this.root.K[0] = newKey;
					this.root.P[0] = x;
					this.root.P[1] = y;
					this.root.n = 1;
					finished = true;
				}
			}
		} while(!finished);
	}
	
	boolean isTerminalNode(TreeNode t, int m) {
		for(int i=0; i<m+1; i++) {
			if(t.P[i] != null) return false;
		}
		return true;
	}
	
	// ��� ����
	void deleteBT(int m, int oldKey) {
		// oldKey�� �ִ� ����� ��� Ž��, ���ÿ� ��� ����
		stack.clear();
		boolean found = searchPath(this.root, m, oldKey);
		if(!found) {
			System.out.println(String.format("d %d : The key does not exist.", oldKey));
			return; // oldKey �߰� ����, ���� �Ұ�
		}
		
		TreeNode x = stack.pop();
		TreeNode y = null;
		
		
		if(!isTerminalNode(x, m)) { // oldKey�� ���� ��忡�� �߰�
			TreeNode internalNode = x;
			int i = 0;
			while(x.K[i] != oldKey) i++;
			
			stack.push(x);
			
			// ����Ű�� ��ġ Ž�� 
			searchPath(x.P[i + 1], m, x.K[i]);
			
			// ����Ű�� oldKey ��ȯ
			x = stack.pop(); // x = ����Ű�� �ִ� �ܸ� ���
			int tmp = internalNode.K[i];
			internalNode.K[i] = x.K[0];
			x.K[0] = tmp; // x.K[0] = oldKey
		}
		boolean finished = false;
		deleteKey(m, x, oldKey); // ��� x���� oldKey ����
		
		if(!stack.isEmpty()) y = stack.pop(); // y�� x�� �θ� ���
		
		do {
			if(this.root == x || x.n >= (m - 1) / 2) finished = true; // underflow �߻� x
			else { // underflow �߻�
				// Ű ��й� �Ǵ� ��� �պ��� ���� ���� ��� ����
				int bs = bestSibling(m, x, y);
				
				if(y.P[bs].n > (m - 1) / 2) { // bestSibling���� ��й�
					redistributeKeys(m, x, y, bs);
					finished = true;
				} else { // bestSibling�� ��� �պ�
					mergeNode(m, x, y, bs);
					x = y;
					if(!stack.isEmpty()) y = stack.pop();
					else finished = true;	
				}
			}
		} while(!finished);
		
		if(y != null && y.n == 0) { // y�� key�� ���� ���(����ִ� ���)
			this.root = y.P[0];
		}
	}
	
	// inorder ��ȸ �˰���
	void inorderBT(TreeNode T, int m) {
		if(T != null && T.n != 0)
			for(int i=0; i < m; i++) {
				inorderBT(T.P[i], m);
				if(i < T.n)
					System.out.print(T.K[i] + " ");
			}
	}
	
	public static void main(String[] args) throws IOException {
		BT test3 = new BT();
		BT test4 = new BT();
		
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\alina\\Desktop\\Java\\File Processing\\src\\BT-input.txt"));
		
		while(true) {
			String line = br.readLine();
			if(line == null) break;
			String[] llist = line.split(" ");
			
			if(llist[0].equals("i")) 
				test3.insertBT(3, Integer.parseInt(llist[1]));
			else
				test3.deleteBT(3, Integer.parseInt(llist[1]));
			test3.inorderBT(test3.root, 3);
			System.out.println();
		}
		
		br = new BufferedReader(new FileReader("C:\\Users\\alina\\Desktop\\Java\\File Processing\\src\\BT-input.txt"));
		
		while(true) {
			String line = br.readLine();
			if(line == null) break;
			String[] llist = line.split(" ");
			
			if(llist[0].equals("i")) 
				test4.insertBT(4, Integer.parseInt(llist[1]));
			else
				test4.deleteBT(4, Integer.parseInt(llist[1]));
			test4.inorderBT(test4.root, 4);
			System.out.println();
		}
	}
}

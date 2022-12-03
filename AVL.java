//���� : ����
//���� : ����
// java���� call by reference�ϰų� ���� ���� �� ���� return�� �� �ִ� ����� ã�� ���� 
// changeBalance�� rotateTree���� ���̴� p, q, rotationType�� AVL���� ���� ������ ������ �ξ� ����߽��ϴ�.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

//class TreeNode{ // node Ŭ����
	Object key;
	int height = 1;
	int bf = 0;
	TreeNode left, right;
	
	TreeNode(TreeNode L, Object key, TreeNode R){
		this.left = L;
		this.right = R;
		this.key = key;
	}
}

public class AVL {
	TreeNode root = getBSTNode(null); // root ��� 
	int cntNodes = 0; // noNode���� ������� �� �� �� ���
	Deque<TreeNode> stack = new ArrayDeque<>(); // use in noNodes, insertBST
	TreeNode p, q; // p = �ұ����� �߻��� ���, q = �ұ����� �߻��� ����� �θ� ���
	TreeNode deleteKeyP; // bf ����� ���� searchNode�� stack���鶧 ���ڷ� �� ����
	int rotationType; // 0 = NO, 1 = LL, 2 = LR, 3 =RL, 4 = RR
	
	TreeNode getBSTNode(Object key) { // �־��� key���� ���� ��� ����
		return new TreeNode(null, key, null);
	}
	
	private int max(int height, int height2) {
		return (height > height2) ? height : height2;
	}
	
	int insertBST(TreeNode T, Object newKey) { // ���� ���� 1, Ű �������� ���� �� 0
		stack.clear();
		TreeNode q = null;
		TreeNode p = T;
		
		if(T.key == null) {
			T.key = newKey; // tree ó�� ������ ���
			return 1;
		}
				
		// find position to insert newKey
		while(p != null) {
			if(newKey == p.key) {
				System.out.println(String.format("i %d : The key already exists.", (int)newKey));
				return 0;
			} // �̹� key���� ���� ��� ������ ���
			
			q = p;
			stack.push(q);
			
			if((int)newKey < (int)p.key) p = p.left;
			else p = p.right;
		}
		
		// create newNode
		TreeNode newNode = this.getBSTNode(newKey);
		
		// insert newNode as a child of q
		if((int)newKey < (int)q.key) q.left = newNode;
		else q.right = newNode;
		
		// update height
		while(!stack.isEmpty()) {
			q = stack.pop();
			q.height = updateHeight(q); // �Լ� ����ؼ� ���� ������Ʈ	
		}
		return 1;
	}
	
	// key���� ���� node Ž��, stack�� ��� ����
	void searchNode(Object key) { // checkBalance�� ���
		if(key == null) return; // key�� null�� ���, �ƹ� ����� ���� �ʰ� return
		TreeNode findNode = this.root;
		while(findNode.key != key) { // key���� ���� ��带 ã���� ��� �ߴ�
			stack.push(findNode); // stack�� ��� ����
			if(findNode.left == null && findNode.right == null) return; // ã�� ���� ���, �ܸ���忡 �������� ��� �ߴ�
			else if((int)findNode.key < (int)key) findNode = findNode.right; // ã�� ��尡 ���� ����� �����ʿ� ���� ���
			else findNode = findNode.left; // ã�� ��尡 ���� ����� ���ʿ� ���� ���
		}
		stack.push(findNode); // ã�� ��� stack�� push
	}
	
	int updateHeight(TreeNode T) { // ���� ����
		int lh = (T.left == null) ? 0 : T.left.height; // left��尡 �������� ���� ��� ó��
		int rh = (T.right == null) ? 0 : T.right.height; // right��尡 �������� ���� ��� ó��
		return max(lh, rh) + 1;
	}
	
	int updateBf(TreeNode T) { // balance factor ����
		int lh = (T.left == null) ? 0 : T.left.height; // left ��尡 �������� ���� ��� ó��
		int rh = (T.right == null) ? 0 : T.right.height; // right ��尡 �������� ���� ��� ó��
		return lh - rh;
	}
	
	// T = this.root�� ��ü, p, q, rotationType�� Ŭ���� �� ������ �ξ� return
	void checkBalance(Object newKey) { // BF �ٽ� ����Ͽ� �ұ��� ���(p)�� ���� �θ���(q), rotationType�� ����
		while(!stack.isEmpty()) { // stack�� ����� ��θ� �̿��� BF �ٽ� ��� -> �� �Լ� ȣ�� �� stack�� ��� setting�ص־� ��(searchNode, minNode, maxNode �� ���)
			TreeNode n = stack.pop();
			n.bf = updateBf(n); // stack���� �̾Ƴ� ����� bf ����
			if(Math.abs(n.bf) > 1) { // �ұ��� ��� ������ ���
				p = n; // �ұ��� ���
				if(!stack.isEmpty()) q = stack.pop(); // �θ� ���
				else q = null; // �ұ��� ��尡 root�� ���
				this.rotationType = findRotationType(p, newKey);
				return;
			}
		} // �ұ��� ��尡 �������� ���� ���
		this.p = null; 
		this.q = null; 
		this.rotationType = 0;
	}
	
	int findRotationType(TreeNode n, Object key) { // rotationType ���� -> int������ ���� �� ��� �� string���� ��ȯ
		int lBf = (n.left == null) ? 0 : n.left.bf; // n.left�� �������� ���� �� ���Ƿ� �� ����
		int rBf = (n.right == null) ? 0 : n.right.bf; // n.right�� �������� ���� �� ���Ƿ� �� ����
		
		if(n.bf > 1) {
			if(lBf >= 0) return 1; // LL 
			else return 2; // LR
		}
		if(n.bf < -1) {
			if(rBf <= 0) return 3; // RR
			else return 4; // RL
		}
		else return 0;
	}
	
	// T = this.root�� ��ü, p,q,rotationType Ŭ���� �� ������ ����Ǿ�����.
	void rotateTree() { // ȸ����Ű�� �Լ�
		TreeNode a = this.p;
		TreeNode b = null;
		TreeNode c = null;
		if(this.rotationType == 1) { // LL
			b = a.left;		
			
			a.left = b.right;
			b.right = a;
			
			a.height = updateHeight(a); // ���� ������Ʈ
			b.height = updateHeight(b);
			
			a.bf = updateBf(a); // bf ����
			b.bf = updateBf(b);
		}
		
		else if(this.rotationType == 2) { // LR
			b = p.left;
			c = b.right;	
			
			b.right = c.left;
			a.left = c.right;
			c.left = b;
			c.right = a;
			
			a.height = updateHeight(a);
			b.height = updateHeight(b);
			c.height = updateHeight(c);
			
			a.bf = updateBf(a);
			b.bf = updateBf(b);
			c.bf = updateBf(c);
			
			b = c;
		}
		
		else if(this.rotationType == 3) { // RR
			b = a.right;
			
			a.right = b.left;
			b.left = a;
			
			a.height = updateHeight(a);
			b.height = updateHeight(b);
			
			a.bf = updateBf(a);
			b.bf = updateBf(b);
		}
		
		else { // RL
			b = p.right;
			c = b.left;
			
			b.left = c.right;
			a.right = c.left;
			c.left = a;
			c.right = b;

			a.height = updateHeight(a);
			b.height = updateHeight(b);
			c.height = updateHeight(c);
			
			a.bf = updateBf(a);
			b.bf = updateBf(b);
			c.bf = updateBf(c);
			
			b = c;
		}
		
		if(q == null) { // root ��尡 �ұ��� ��忴�� ���
			this.root = b;	// ȸ����Ų ���� root�� �缳��
			q = a;
			stack.push(this.root); // bf������Ʈ�� ���� stack�� push
		}
		else if((int)a.key < (int)this.q.key)// p�� �θ���(q)�� ���ʿ� ����Ǿ��ִ� ���
			this.q.left = b;
		else // p�� �θ���(q)�� �����ʿ� ����Ǿ��ִ� ���
			this.q.right = b;
		
		q.height = updateHeight(q); // �θ� ��� ���� ����
		
		while(true) { // stack�� �����ִ� ��� -> �ұ��� ���� ���� �θ� ��带 ������ ��ο� �ش�Ǵ� ��� bf ����
			this.q.bf = updateBf(q);
			if(stack.isEmpty()) break;
			this.q = stack.pop();
		}
	}
	
	int insertAVL(Object newKey) { //���� ���� == 0 return
		int i = insertBST(this.root, newKey);
		if(i == 0) return i; // Ű�� �̹� ������ ���
		searchNode(newKey); // stack ����
		checkBalance(newKey); // bf ���, �ұ��� ��� Ž��
		if(this.rotationType != 0) rotateTree(); //rotation ����
		return i;
	}
	
	int height(TreeNode T) { // node ����
		return T.height;
	}
	
	int noNodes(TreeNode T) { // ���� ��� ����
		if(T != null) {
			noNodes(T.left);
			cntNodes += 1;
			noNodes(T.right);
		}
		return cntNodes-1;
		
	}
	
	TreeNode maxNode(TreeNode T) { // ���� ��� �� ���� ���� ū ���
		TreeNode tmp = T;
		while(tmp.right != null) {
			stack.push(tmp);
			tmp = tmp.right;
		}
		return tmp;
	}
	
	TreeNode minNode(TreeNode T) { // ���� ��� �� ���� ���� ���� ���
		TreeNode tmp = T;
		while(tmp.left != null) {
			stack.push(tmp);
			tmp = tmp.left;
		}
		return tmp;
	}
	
	int deleteBST(TreeNode T, Object deleteKey) { // ���� ���� 1, ���� 0
		TreeNode p = T;
		TreeNode q = null;
		stack.clear();
		
		//find position
		while(p != null && !deleteKey.equals(p.key)) {
			q = p;
			stack.push(q);
			
			if((int)deleteKey < (int)p.key) p = p.left;
			else p = p.right;
		}
		
		
		// deleteKey was not found
		if(p == null) {
			deleteKeyP = null;
			System.out.println(String.format("d %d : The key does not exist.", (int)deleteKey));
			return 0;
		}
					
		// case of degree 2
		if(p.left != null && p.right != null) {
			stack.push(p);
			TreeNode tmpNode = p;
			if(height(p.left) <= height(p.right)) 
				p = this.minNode(p.right);
			else if(height(p.left) > height(p.right)) 
				p = this.maxNode(p.left);
			else {
				if(noNodes(p.left)>=noNodes(p.right)) 
					p = this.maxNode(p.left);
				else 
					p = this.minNode(p.right);
			}
			tmpNode.key = p.key;		
		}
		q = stack.peekFirst(); // �θ��� ���� -> stack���� �̾Ƴ��� �ȵǹǷ� peekFirst ���
		deleteKeyP = q; // bf ����ϱ����� searchNode�Լ��� ���, �� �� ���ڷ� ���� ����
										// maxNode, minNode�� �θ� ���	
		
		// case of degree 0
		if(p.left == null && p.right == null) {
			if(q == null) this.root = null; //case of root
			else if(q.left == p) q.left = null;
			else q.right = null;
		}
		else{ // case of degree 1
			if(p.left != null) {
				if(q == null) this.root = T.left; // case of root
				else if(q.left == p) q.left = p.left;
				else q.right = p.left;
			}
			else {
				if(q == null) this.root = T.right; // case of root
				else if(q.left == p) q.left = p.right;
				else q.right = p.right;
			}
		}
		
		// update height
		while(!stack.isEmpty()) {
			q = stack.pop();
			q.height = updateHeight(q);
		}
		return 1;
	}
	
	int deleteAVL(Object newKey) { // ���� ���� == 0
		int d = deleteBST(this.root, newKey);
		if(d == 0) return d; // Ű�� �������� ���� ���
		if(deleteKeyP != null) {
			searchNode(deleteKeyP.key); // deleteKeyP.key������ ��� ���� ����
			checkBalance(deleteKeyP.key);
		}
		if(this.rotationType != 0) rotateTree();
		return d;
	}
	
	String rtToString() { // rotationType String���� ��ȯ - ����ϱ� ���� ���Ǵ� �Լ�
		String rotation = "";
		switch(this.rotationType) {
		case 0:
			rotation = "NO";
			break;
		case 1:
			rotation = "LL";
			break;
		case 2:
			rotation = "LR";
			break;
		case 3:
			rotation = "RR";
			break;
		case 4:
			rotation = "RL";
			break;
		}
		
		return rotation;
	}
	
	void inorderBST(TreeNode T) { // ��ȸ
		if(T != null && T.key != null) {
			inorderBST(T.left);
			System.out.print("(" + T.key + ", " + T.bf + ") ");
			inorderBST(T.right);
		}
	}
	
	public static void main(String[] args) throws IOException {
		AVL test = new AVL();
		int a = 0;
		int b = 0;
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\alina\\Desktop\\Java\\File Processing\\src\\AVL-input.txt"));
		
		while(true) {
			String line = br.readLine();
			if(line == null) break;
			String[] llist = line.split(" ");
			
			if(llist[0].equals("i")) {
				a = test.insertAVL(Integer.parseInt(llist[1]));
				if(a != 0) System.out.print(test.rtToString() + " ");
			}
			else {
				b = test.deleteAVL(Integer.parseInt(llist[1]));
				if(b != 0) System.out.print(test.rtToString() + " ");
			}
			
			test.inorderBST(test.root);
			System.out.println();
		}
	}
}

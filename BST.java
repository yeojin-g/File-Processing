// 삽입: 성공
// 삭제: 성곰

import java.util.Deque;
import java.util.ArrayDeque;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//class TreeNode {
	Object key;
	int height = 1;
	TreeNode left;
	TreeNode right;
	
	TreeNode(TreeNode L, Object key, TreeNode R){
		this.left = L;
		this.right = R;
		this.key = key;
	}
}

public class BST {
	TreeNode root = getBSTNode(null);
	int cntNodes = 0;
	Deque<TreeNode> stack = new ArrayDeque<>(); // use in noNodes, insertBST
	
	TreeNode getBSTNode(Object key) {
		return new TreeNode(null, key, null);
	}
	
	private int max(int height, int height2) {
		return (height > height2) ? height : height2;
	}
	
	void insertBST(TreeNode T, Object newKey) {
		stack.clear();
		TreeNode q = null;
		TreeNode p = T;
		
		if(T.key == null) {
			T.key = newKey; // tree 처음 생성할 경우
			return;
		}
				
		// find position to insert newKey
		while(p != null) {
			if(newKey == p.key) {
				System.out.println(String.format("i %d : The key already exists.", (int)newKey));
				return;
			} // 이미 key값을 가진 노드 존재할 경우
			
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
			if(q.right != null && q.left != null)
				q.height = 1 + max(q.left.height, q.right.height);
			else if(q.right == null && q.left != null)
				q.height = 1 + q.left.height;
			else if(q.right != null && q.left == null)
				q.height = 1 + q.right.height;
			else continue;	
		}
	}
	
	int height(TreeNode T) {
		return T.height;
	}
	
	int noNodes(TreeNode T) { // 하위 노드 개수
		if(T != null) {
			noNodes(T.left);
			cntNodes += 1;
			noNodes(T.right);
		}
		return cntNodes-1;
		
	}
	
	TreeNode maxNode(TreeNode T) { // 하위 노드 중 제일 값이 큰 노드
		TreeNode tmp = T;
		while(tmp.right != null) {
			stack.push(tmp);
			tmp = tmp.right;
		}
		return tmp;
	}
	
	TreeNode minNode(TreeNode T) { // 하위 노드 중 제일 값이 작은 노드
		TreeNode tmp = T;
		while(tmp.left != null) {
			stack.push(tmp);
			tmp = tmp.left;
		}
		return tmp;
	}
	
	void deleteBST(TreeNode T, Object deleteKey) {
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
			System.out.println(String.format("d %d : The key does not exist.", (int)deleteKey));
			return;
		}
					
		// case of degree 2
		if(p.left != null && p.right != null) {
			stack.push(p);
			TreeNode tmpNode = p;
			if(height(p.left) < height(p.right)) p = this.minNode(p.right);
			else if(height(p.left) > height(p.right)) p = this.maxNode(p.left);
			else {
				if(noNodes(p.left)>=noNodes(p.right)) p = this.maxNode(p.left);
				else p = this.minNode(p.right);
			}
			
			tmpNode.key = p.key;
			q = stack.pop(); // top of stack
		}
		
		// case of degree 0
		if(p.left == null && p.right == null) {
			if(q == null) this.root.key = null; //case of root
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
			if(q.right != null && q.left != null)
				q.height = 1 + max(q.left.height, q.right.height);
			else if(q.right == null && q.left != null)
				q.height = 1 + q.left.height;
			else if(q.right != null && q.left == null)
				q.height = 1 + q.right.height;
			else continue;
		}
	}
	
	void inorderBST(TreeNode T) {
		if(T != null && T.key != null) {
			inorderBST(T.left);
			System.out.print(T.key + " ");
			inorderBST(T.right);
		}
	}
	
	public static void main(String[] args) throws IOException {
		BST test = new BST();
		
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\alina\\Desktop\\Java\\File Processing\\src\\BST-input.txt"));
		
		while(true) {
			String line = br.readLine();
			if(line == null) break;
			String[] llist = line.split(" ");
			
			if(llist[0].equals("i")) 
				test.insertBST(test.root, Integer.parseInt(llist[1]));
			else
				test.deleteBST(test.root, Integer.parseInt(llist[1]));
			test.inorderBST(test.root);
			System.out.println();
		}
	}
}

import org.junit.*;
import java.util.*;
import play.test.*;
import java.util.List;
import models.*;

public class BasicTest extends UnitTest {

	@Before
		public void setup() {
			Fixtures.deleteDatabase();
			Fixtures.loadModels("data.yml");
		}
	@Test
		public void aVeryImportantThingToTest() {
			assertEquals(2, 1 + 1);
		}
	@Test
		public void createAndRetrieveUser(){
			//new User("bob@gmail.com","secret","Bob").save();

			User bob=User.find("byEmail","bob@gmail.com").first();

			assertNotNull(bob);
			assertEquals("Bob",bob.fullname);
		}
	@Test
		public void tryConnectAsUser() {
			//new User("bob@gmail.com","secret","Bob").save();

			assertNotNull(User.connect("bob@gmail.com","secret"));
			assertNull(User.connect("bob@gmail.com","aaa"));
			assertNull(User.connect("tom@gmail.com","sercet"));
		}
	@Test
		public void createPost(){
			//User bob=new User("bob@gmail.com","sercet","Bob").save();
			User bob=User.find("byFullname","Bob").first();
			new Post(bob,"My first Post","Hello world").save();

			assertEquals(1, Post.count());

			List<Post> bobPosts=Post.find("byAuthor",bob).fetch();
			assertEquals(1, bobPosts.size());
			Post firstPost=bobPosts.get(0);
			assertNotNull(firstPost);
			assertEquals(bob,firstPost.author);
			assertEquals("My first Post",firstPost.title);
			assertEquals("Hello world",firstPost.content);
			assertNotNull(firstPost.postedAt);

		}
	@Test
		public void postComment(){

			//User bob=new User("bob@gmail.com","sercet","Bob").save();

			User bob=User.find("byFullname","Bob").first();
			Post bobPost=new Post(bob,"My first Post","Hello world").save();

			new Comment(bobPost,"jeff","Nice blog").save();
			new Comment(bobPost,"Tom","I know play").save();

			List<Comment> bobPostComments = Comment.find("byPost",bobPost).fetch();

			assertEquals(2, bobPostComments.size());
			
			Comment firstComment = bobPostComments.get(0);
			assertNotNull(firstComment);
			assertEquals("jeff",firstComment.author);
			assertEquals("Nice blog",firstComment.content);
			assertNotNull(firstComment.postedAt);

			
			Comment secondComment = bobPostComments.get(1);
			assertNotNull(secondComment);
			assertEquals("Tom",secondComment.author);
			assertEquals("I know play",secondComment.content);
			assertNotNull(secondComment.postedAt);
		}
	@Test
		public void useTheCommentsRelation(){

			//User bob=new User("bob@gmail.com","sercet","Bob").save();

			User bob=User.find("byFullname","Bob").first();
			Post bobPost=new Post(bob,"My first Post","Hello world").save();

			bobPost.addComment("jeff","Nice blog");
			bobPost.addComment("Tom","I know play");

			assertEquals(1, User.count());
			assertEquals(1, Post.count());
			assertEquals(2,Comment.count());

			bobPost = Post.find("byAuthor",bob).first();
			assertNotNull(bobPost);

			assertEquals(2,bobPost.comments.size());
			assertEquals("jeff",bobPost.comments.get(0).author);

			bobPost.delete();

			
			assertEquals(1, User.count());
			assertEquals(0, Post.count());
			assertEquals(0,Comment.count());
		}
	@Test
		public void testTags(){
			User bob=User.find("byFullname","Bob").first();
			Post bobPost= new Post(bob,"My first post","Hello world").save();
			Post anotherBobPost=new Post(bob, "Hop","Hello world").save();

			assertEquals(0,Post.findTaggedWith("Red").size());

			bobPost.tagItWith("Red").tagItWith("Blue").save();
			anotherBobPost.tagItWith("Red").tagItWith("Green").save();

			assertEquals(2,Post.findTaggedWith("Red").size());
			assertEquals(1,Post.findTaggedWith("Blue").size());
			assertEquals(1,Post.findTaggedWith("Green").size());
			assertEquals(0,Post.findTaggedWith("Red","Green","Blue").size());
			List<Map> cloud = Tag.getCloud();
			assertEquals("[{tag=Blue,pound=1},{tag=Green,pound=1},{tag=Red,pound=2}]",cloud.toString());

		}
			
}

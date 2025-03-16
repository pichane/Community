import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlinx.coroutines.runBlocking

class GetItemsUseCaseTest {

    private lateinit var getItemsUseCase: GetItemsUseCase
    private lateinit var itemRepository: ItemRepository

    @Before
    fun setUp() {
        itemRepository = mock(ItemRepository::class.java)
        getItemsUseCase = GetItemsUseCase(itemRepository)
    }

    @Test
    fun `test get items returns expected result`() = runBlocking {
        val expectedItems = listOf(Item(id = 1, name = "Item 1"), Item(id = 2, name = "Item 2"))
        `when`(itemRepository.getItems()).thenReturn(expectedItems)

        val result = getItemsUseCase.execute()

        assertEquals(expectedItems, result)
    }
}
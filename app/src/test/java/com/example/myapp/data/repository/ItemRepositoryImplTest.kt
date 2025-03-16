import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class ItemRepositoryImplTest {

    private lateinit var itemRepository: ItemRepositoryImpl
    private lateinit var mockItemDao: ItemDao
    private lateinit var mockApiService: ApiService

    @Before
    fun setUp() {
        mockItemDao = mock(ItemDao::class.java)
        mockApiService = mock(ApiService::class.java)
        itemRepository = ItemRepositoryImpl(mockItemDao, mockApiService)
    }

    @Test
    fun `test getItems returns items from local database`() {
        val mockItems = listOf(ItemEntity(1, "Test Item"))
        `when`(mockItemDao.getAllItems()).thenReturn(mockItems)

        val items = itemRepository.getItems()

        assertEquals(mockItems.size, items.size)
        assertEquals(mockItems[0].name, items[0].name)
    }

    @Test
    fun `test fetchAndStoreItems fetches items from API and stores them in database`() {
        val mockApiResponse = listOf(ItemResponse(1, "Test Item"))
        `when`(mockApiService.getItems()).thenReturn(mockApiResponse)

        itemRepository.fetchAndStoreItems()

        verify(mockItemDao).insertItems(anyList())
    }
}
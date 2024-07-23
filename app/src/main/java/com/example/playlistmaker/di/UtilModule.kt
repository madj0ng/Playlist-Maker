import com.example.playlistmaker.data.converters.AlbumModelConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.converters.TrackSharedConvertor
import com.example.playlistmaker.util.DebounceUtils
import com.example.playlistmaker.util.FormatUtils
import org.koin.dsl.module

val utilModule = module {
    factory {
        DebounceUtils
    }

    factory {
        FormatUtils
    }

    factory {
        TrackSharedConvertor()
    }

    factory {
        AlbumModelConverter
    }

    factory{
        TrackDbConverter
    }
}